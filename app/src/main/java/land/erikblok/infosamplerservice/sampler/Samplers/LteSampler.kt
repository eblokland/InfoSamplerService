package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import android.os.Looper
import android.os.SystemClock
import android.telephony.PhoneStateListener
import android.telephony.PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import land.erikblok.infosamplerservice.sampler.Logs.CellularSignalStrengthLog
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog

private const val TAG = "CELL_SAMPLER"

class LteSampler(ctx: Context, samplerScope: CoroutineScope) : BaseSampler(ctx, samplerScope) {

    private val events = LISTEN_SIGNAL_STRENGTHS
    val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    @Suppress("DEPRECATION") // not deprecated for target api
    override val logSource: Flow<SamplerLog> = callbackFlow {

        //TODO: This works but is bad, it will leak threads everywhere.
        //I should probably create a custom callback api that can hook into
        //a singleton thread and then convert that into a flow
        //There is a reason this got deprecated...
        val thread = object : Thread() {
            private lateinit var psl : PhoneStateListener

            override fun run() {
                Looper.prepare()
                psl = object: PhoneStateListener() {
                    override fun onSignalStrengthsChanged(signalStrength: SignalStrength?) {
                        signalStrength?.let {
                            trySendBlocking(
                                CellularSignalStrengthLog(SystemClock.elapsedRealtimeNanos(), it.level)
                            ).onFailure { Log.d(TAG, "Failed to send cell strength log") }
                        }
                    }
                }
                tm.listen(psl, events)
                Looper.loop()
            }

            fun deregister() {
                if(::psl.isInitialized){
                    tm.listen(psl, PhoneStateListener.LISTEN_NONE)
                }
            }
        }

        thread.start()
        awaitClose{
            thread.deregister()
        }
    }


}