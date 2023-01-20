package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog
import land.erikblok.infosamplerservice.sampler.Logs.VoltageLog
import java.util.concurrent.locks.Lock

private val TAG = "INTENT_SAMPLER"

abstract class IntentSampler(ctx: Context, samplerScope: CoroutineScope) :
    BaseSampler(ctx, samplerScope), DestructableSampler {

    protected abstract val intentFilter: IntentFilter

    private var br: BroadcastReceiver? = null


    override val logSource: Flow<SamplerLog> = callbackFlow {
        br = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    trySendBlocking(
                        createLog(it)
                    ).onFailure { Log.d(TAG, "Failed to send intent log") }
                }
            }
        }
        ctx.registerReceiver(br, intentFilter)

        awaitClose {
            unregisterReceiver()
        }
    }

    override fun onDestroy() {
       unregisterReceiver()
    }

    private fun unregisterReceiver(){
            //this might get double-called depending on the race condition with destruction.
            synchronized(this) {
                if (br != null) {
                    Log.d(TAG, "Unregistering")
                    try {
                        ctx.unregisterReceiver(br)
                        br = null
                        Log.d(TAG, "Unregistered")
                    } catch (e: IllegalArgumentException) {
                        Log.d(TAG, "threw from unregisterReceiver", e)
                    }
                }
            }
    }



    protected abstract fun createLog(intent: Intent): SamplerLog
}