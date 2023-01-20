package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog

class ContentObserverSampler(ctx: Context, samplerScope: CoroutineScope) : BaseSampler(ctx,
    samplerScope
) {
    override val logSource: Flow<SamplerLog> = callbackFlow {

    }

}