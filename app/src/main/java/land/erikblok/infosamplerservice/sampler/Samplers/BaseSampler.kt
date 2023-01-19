package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog


abstract class BaseSampler(ctx: Context, samplerScope: CoroutineScope) {
    protected var isConfigured = false
    protected var isActive = false
    protected abstract val logSource : Flow<SamplerLog>

    //lazy init so that it will not (i think...) attempt to use the logSource until child implementation has been inited
    //i think all of the logFlows will use the same shareIn setup, so there's no need to reimplement that
    //in every class
    open val logFlow : SharedFlow<SamplerLog> by lazy{logSource.shareIn(samplerScope, SharingStarted.WhileSubscribed())}
    //protected var receivers = LinkedHashSet<BaseLogReceiver>()

    //internal configuration that will run whenever
    //we start the sampler.
    protected abstract suspend fun configureSampler()
}