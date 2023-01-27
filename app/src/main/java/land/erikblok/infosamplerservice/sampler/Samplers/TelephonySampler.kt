package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.telephony.PhoneStateListener
import android.telephony.PhoneStateListener.LISTEN_NONE
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog

abstract class TelephonySampler(ctx: Context, samplerScope: CoroutineScope) : BaseSampler(ctx, samplerScope) {

    abstract var psl : PhoneStateListener
    abstract var events : Int


    @Suppress("DEPRECATION") // not deprecated for target api
    override val logSource: Flow<SamplerLog> = callbackFlow {
        val tm = ctx.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        tm.listen(psl, events)

        awaitClose{
            tm.listen(psl, LISTEN_NONE)
        }
    }


}
