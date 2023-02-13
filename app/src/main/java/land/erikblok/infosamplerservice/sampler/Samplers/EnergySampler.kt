package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import android.os.BatteryManager
import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import land.erikblok.infosamplerservice.sampler.Logs.EnergyLog
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog

class EnergySampler(ctx: Context, samplerScope: CoroutineScope) : PollingSampler<Long>(ctx,
    samplerScope
){
    override lateinit var funToSample : () -> Long
    private var batteryManager : BatteryManager

    init{
        batteryManager = ctx.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        funToSample = {   batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER) }
    }

    override fun createLog(sample: Long): SamplerLog {
        return EnergyLog(SystemClock.uptimeMillis(), sample)
    }
}