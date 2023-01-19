package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog

class IntentSampler(ctx: Context, samplerScope: CoroutineScope) : BaseSampler(ctx, samplerScope,) {
    override val logSource: Flow<SamplerLog>
        get() = TODO("Not yet implemented")

    override suspend fun configureSampler() {
        TODO("Not yet implemented")
    }
}