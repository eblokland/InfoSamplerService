package land.erikblok.infosamplerservice

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.net.toFile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import land.erikblok.infosamplerservice.sampler.LogReceivers.LogFileWriter
import land.erikblok.infosamplerservice.sampler.LogReceivers.LogcatWriter
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog
import land.erikblok.infosamplerservice.sampler.Samplers.*
import java.io.File

const val ACTION_WRITETOFILE = "land.erikblok.action.WRITETOFILE"
const val ACTION_WRITETOLOGCAT = "land.erikblok.action.WRITETOLOGCAT"
const val ACTION_NOWRITE = "land.erikblok.action.NOWRITE"
const val ACTION_STOPFILE = "land.erikblok.action.STOPFILE"
const val ACTION_STOPLOGCAT = "land.erikblok.action.STOPLOGCAT"
const val ACTION_NOKEEPALIVE = "land.erikblok.action.NOKEEPALIVE"

class EnvironmentSampler : Service() {

    private lateinit var samplers: MutableSet<BaseSampler>
    private lateinit var fileWriter: LogFileWriter
    private lateinit var logcatWriter: LogcatWriter
    private var loggerId = -1;
    private var logcatId = -1;
    private var keepaliveId = -1;


    private val dirtyHardcodedFilePath = "/data/local/tmp/logs.txt"
    private val TAG = "SAMPLER_SERVICE"

    private lateinit var textLoggerScope: CoroutineScope
    private lateinit var logcatLoggerScope: CoroutineScope
    private val serviceScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default) //scope for running the samplers, etc.  Will be kept alive until the service is killed.

    inner class SamplerBinder : Binder() {
        fun getService(): EnvironmentSampler = this@EnvironmentSampler
    }

    //region SERVICE_INTERFACE

    override fun onCreate() {
        Log.d(TAG, "called oncreate")
        samplers = LinkedHashSet()
        setupSamplers(samplers)
        textLoggerScope =
            CoroutineScope(Job() + Dispatchers.Default) //is job() the best way to do this?
    }

    //for now this will just ignore multiple starts, so don't do that.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "called onstart")
        intent?.let {
            when (intent.action) {
                ACTION_WRITETOLOGCAT -> runLogcatLogger(startId)
                ACTION_WRITETOFILE -> runFileLogger(startId, intent)
                ACTION_NOWRITE, ACTION_MAIN ->  keepaliveId = startId
                // this is stupid but maybe the best way to send commands via adb
                //always consider stop actions as failed
                ACTION_STOPFILE -> stopFileLogger()
                ACTION_STOPLOGCAT -> stopLogcat()
                ACTION_NOKEEPALIVE -> keepaliveId = -1
                else ->  false
            }
        }

        //go ahead and stop if we don't have any active things.
        if(logcatId == -1 && loggerId == -1 && keepaliveId == -1){
            stopSelf()
        }
        //at this point don't bother restarting the service if it gets killed for oom
        return START_NOT_STICKY
    }


    override fun onDestroy() {
        stopLogcat()
        stopFileLogger()
        serviceScope.cancel()
        //manually destroy samplers because we have no guarantee that cancel runs before the service is destroyed :(
        samplers.forEach { if (it is DestructableSampler) it.onDestroy() }
        super.onDestroy()
    }


    override fun onBind(intent: Intent): IBinder {
        return SamplerBinder()
    }

    //endregion


    //region PUBLIC_INTERFACE

    fun getSamplers(): List<BaseSampler> {
        return samplers.toList()
    }

    fun getFlows(): List<SharedFlow<SamplerLog>> {
        return samplers.map { sampler -> sampler.logFlow }
    }

    fun stopLogcat() {
        if(logcatId == -1) return
        logcatLoggerScope.cancel()
        logcatId = -1
    }
    fun stopFileLogger(){
        if(loggerId == -1) return
        if (this::fileWriter.isInitialized) fileWriter.onDestroy()
        textLoggerScope.cancel()
        loggerId = -1;
    }

    fun runFileLogger(startId: Int, intent: Intent): Boolean {
        if (loggerId != -1) return false
        val uri = intent.data ?: return false

        val file: File
        try {
            file = uri.toFile();
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "somehow invalid file from intent")
            return false
        }

        try {
            fileWriter = LogFileWriter(file)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to init fileWriter", e)
            return false
        }
        samplers.forEach { s ->
            textLoggerScope.launch {//this should automatically get killed once coroutine scope is cleaned up
                s.logFlow.collect {
                    fileWriter.RecordLog(it)
                }
            }
        }
        loggerId = startId
        return true
    }

    fun runLogcatLogger(startId: Int): Boolean {
        if (logcatId != -1) return false;
        logcatLoggerScope = CoroutineScope(Job() + Dispatchers.Default)
        Log.i(TAG, "called run logcat logger")
        logcatWriter = LogcatWriter()
        samplers.forEach { s ->
            logcatLoggerScope.launch {
                s.logFlow.collect {
                    logcatWriter.RecordLog(it)
                }
            }
        }
        logcatId = startId
        return true
    }

    //endregion

    //region PRIVATE_HELPERS



    private fun setupSamplers(set: MutableSet<BaseSampler>) {
        set.add(
            CurrentSampler(
                this,
                serviceScope
            )
        ) //pass context of service to sampler, make sure this is not called before onStart
        set.add(VoltageSampler(this, serviceScope))
        set.add(DisplayManagerSampler(this, serviceScope))
        set.add(LteSampler(this, serviceScope))
        set.add(WifiRoamSampler(this, serviceScope))
    }



    //endregion


}
