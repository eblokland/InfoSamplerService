package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import android.icu.util.UniversalTimeScale.toLong
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import land.erikblok.infosamplerservice.sampler.LogReceivers.BaseLogReceiver
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

abstract class PollingSampler<T>(ctx: Context) : BaseSampler(ctx) {
    protected var updateRate : Duration = Duration.ZERO
    protected var estimatedUpdateRate : Long = 1000
    protected open var samplesPerUpdate : Int = 1

    protected var lastSample : T? = null

    protected abstract var funToSample : () -> T

    private val TAG = "POLLING_SAMPLER"

    override val logFlow : Flow<SamplerLog> = flow{
        if(!isConfigured){
            ConfigureSampler()
        }
        while(true){
            val latest = funToSample()
            if(true){//latest != lastSample){
                var log = createLog(latest)
                emit(log)
                lastSample = latest
                delay(updateRate)
            }
        }
    }

    override suspend fun ConfigureSampler(){
        Log.d(TAG, "configuring polling sampler")
        estimatedUpdateRate = estimateUpdateRateAvg(funToSample)
        updateRate = ((estimatedUpdateRate / (samplesPerUpdate).toDouble()).toDuration(DurationUnit.MILLISECONDS))
        Log.d(TAG, "set update rate to $updateRate")
        isConfigured = true
    }

    protected abstract fun createLog(sample : T) : SamplerLog
}