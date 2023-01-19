package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog
import land.erikblok.infosamplerservice.sampler.Logs.VoltageLog

private val TAG = "VOLTAGE_SAMPLER"

class VoltageSampler(ctx: Context, samplerScope: CoroutineScope) : IntentSampler(
    ctx,
    samplerScope
) {
    override val intentFilter: IntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    override fun createLog(intent: Intent): SamplerLog {
        return VoltageLog(
            SystemClock.elapsedRealtimeNanos(),
            intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        )
    }

}

