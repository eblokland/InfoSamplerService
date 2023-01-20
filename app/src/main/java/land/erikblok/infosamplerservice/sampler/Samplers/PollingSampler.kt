package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import android.os.Debug
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

abstract class PollingSampler<T>(ctx: Context, samplerScope: CoroutineScope) : BaseSampler(
    ctx,
    samplerScope,
) {
    private var updateRate: Duration = Duration.ZERO
    private var estimatedUpdateRate: Long = 1000
    protected var isConfigured = false
    protected open var samplesPerUpdate: Int = 1


    private var lastSample: T? = null

    protected abstract var funToSample: () -> T

    private val TAG = "POLLING_SAMPLER"

    override val logSource = flow {
        if (!isConfigured) {
            configureSampler()
        }
        while (true) {
            val latest = funToSample()
            Log.d(TAG, "Running sampler")
            if (latest != lastSample) {
                var log = createLog(latest)
                emit(log)
                lastSample = latest
            }
            delay(updateRate)
        }
    }

    protected open suspend fun configureSampler() {
        Log.d(TAG, "configuring polling sampler")
        estimatedUpdateRate = estimateUpdateRateAvg(funToSample)
        updateRate =
            ((estimatedUpdateRate / (samplesPerUpdate).toDouble()).toDuration(DurationUnit.MILLISECONDS))
        Log.d(TAG, "set update rate to $updateRate")
        isConfigured = true
    }

    protected abstract fun createLog(sample: T): SamplerLog
}