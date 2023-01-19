package land.erikblok.infosamplerservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import land.erikblok.infosamplerservice.sampler.LogReceivers.LogFileWriter
import land.erikblok.infosamplerservice.sampler.LogReceivers.LogcatWriter
import land.erikblok.infosamplerservice.sampler.Samplers.BaseSampler
import land.erikblok.infosamplerservice.sampler.Samplers.CurrentSampler

class EnvironmentSampler : Service() {

    private lateinit var samplers: MutableSet<BaseSampler>
    private lateinit var fileWriter: LogFileWriter
    private lateinit var logcatWriter: LogcatWriter
    private var loggerStarted = false;
    private var logcatStarted = false;


    private val dirtyHardcodedFilePath = "/data/local/tmp/logs.txt"
    private val TAG = "SAMPLER_SERVICE"

    private lateinit var textLoggerScope: CoroutineScope
    private lateinit var logcatLoggerScope: CoroutineScope
    private val serviceScope : CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default) //scope for running the samplers, etc.  Will be kept alive until the service is killed.


    override fun onCreate() {
        Log.i(TAG, "called oncreate")
        samplers = LinkedHashSet()
        setupSamplers(samplers)
        textLoggerScope =
            CoroutineScope(Job() + Dispatchers.Default) //is job() the best way to do this?
        logcatLoggerScope = CoroutineScope(Job() + Dispatchers.Default)
    }

    //for now this will just ignore multiple starts, so don't do that.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "called onstart")
        runLogcatLogger(startId)
        return START_STICKY
    }

    override fun onDestroy() {
        if (this::fileWriter.isInitialized) fileWriter.onDestroy()
        textLoggerScope.cancel()
        logcatLoggerScope.cancel()
        serviceScope.cancel()
    }


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private fun setupSamplers(set: MutableSet<BaseSampler>) {
        set.add(CurrentSampler(this, serviceScope)) //pass context of service to sampler, make sure this is not called before onStart
    }

    private fun runFileLogger(startId: Int): Boolean {
        if (loggerStarted) return true
        try {
            fileWriter = LogFileWriter(dirtyHardcodedFilePath)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to init fileWriter", e)
            stopSelf(startId) //will this be ignored if something is bound?
            return false
        }
        samplers.forEach { s ->
            textLoggerScope.launch {//this should automatically get killed once coroutine scope is cleaned up
                s.logFlow.collect {
                    fileWriter.RecordLog(it)
                }
            }
        }
        loggerStarted = true
        return true
    }

    private fun runLogcatLogger(startId: Int): Boolean {
        if (logcatStarted) return true;
        Log.i(TAG, "called run logcat logger")
        logcatWriter = LogcatWriter()
        samplers.forEach { s ->
            logcatLoggerScope.launch {
                s.logFlow.collect {
                    logcatWriter.RecordLog(it)
                }
            }
        }
        logcatStarted = true
        return true;
    }


}
