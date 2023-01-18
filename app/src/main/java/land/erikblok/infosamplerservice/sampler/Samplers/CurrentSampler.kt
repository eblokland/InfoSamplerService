package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.os.BatteryManager
import android.os.SystemClock
import kotlinx.coroutines.delay
import land.erikblok.infosamplerservice.sampler.Logs.CurrentLog
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog
import land.erikblok.infosamplerservice.sampler.Logs.VoltageLog

class CurrentSampler(ctx: Context) : PollingSampler<Int>(ctx) {

    private var batteryManager : BatteryManager
    override lateinit var funToSample : () -> Int

    init{
        batteryManager = ctx.getSystemService(BATTERY_SERVICE) as BatteryManager
        funToSample = {   batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) }
    }


    override fun createLog(sample: Int) : SamplerLog{
        return CurrentLog(SystemClock.elapsedRealtimeNanos(), sample)
    }
}