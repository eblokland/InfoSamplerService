package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager.EXTRA_NEW_RSSI
import android.net.wifi.WifiManager.RSSI_CHANGED_ACTION
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog
import land.erikblok.infosamplerservice.sampler.Logs.WifiSignalStrengthLog

private val TAG = "WIFI_STR_SAMPLER"
class WifiStrSampler(ctx: Context, samplerScope: CoroutineScope) : IntentSampler(ctx,
    samplerScope
) {
    override val intentFilter: IntentFilter = IntentFilter(RSSI_CHANGED_ACTION)

    override fun createLog(intent: Intent): SamplerLog? {
        val rssi = intent.getIntExtra(EXTRA_NEW_RSSI, 1)
        if(rssi != 1){
            return WifiSignalStrengthLog(SystemClock.uptimeMillis(), rssi)
        }
        return null
    }
}