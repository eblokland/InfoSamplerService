package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import kotlinx.coroutines.flow.Flow
import land.erikblok.infosamplerservice.sampler.LogReceivers.BaseLogReceiver
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog


abstract sealed class BaseSampler(ctx : Context) {
    var isConfigured = false
    var isActive = false
    abstract val logFlow : Flow<SamplerLog>
    protected var receivers = LinkedHashSet<BaseLogReceiver>()

    //internal configuration that will run whenever
    //we start the sampler.
    protected abstract suspend fun ConfigureSampler()
}