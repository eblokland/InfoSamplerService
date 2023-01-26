package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog


abstract class BaseSampler(protected val ctx: Context, samplerScope: CoroutineScope) {
    protected abstract val logSource : Flow<SamplerLog>

    //lazy init so that it will not (i think...) attempt to use the logSource until child implementation has been inited
    //i think all of the logFlows will use the same shareIn setup, so there's no need to reimplement that
    //in every class
    open val logFlow : SharedFlow<SamplerLog> by lazy{logSource.shareIn(samplerScope, SharingStarted.WhileSubscribed(), replay = 1)}
}