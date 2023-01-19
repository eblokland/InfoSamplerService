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

private val TAG = "INTENT_SAMPLER"
abstract class IntentSampler(ctx: Context, samplerScope: CoroutineScope) : BaseSampler(ctx, samplerScope) {

    protected abstract val intentFilter : IntentFilter


    override val logSource: Flow<SamplerLog> = callbackFlow {
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    trySendBlocking(
                        createLog(it)
                    ).onFailure { Log.d(TAG, "Failed to send intent log") }
                }
            }
        }
        ctx.registerReceiver(broadcastReceiver, intentFilter)
        awaitClose {ctx.unregisterReceiver(broadcastReceiver)}
    }

    protected abstract fun createLog(intent : Intent) : SamplerLog
}