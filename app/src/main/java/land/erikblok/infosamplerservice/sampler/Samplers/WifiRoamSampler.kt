package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.EXTRA_NETWORK_INFO
import android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog
import land.erikblok.infosamplerservice.sampler.Logs.WifiRoamLog

private val TAG = "WIFI_SAMPLER"

class WifiRoamSampler(ctx: Context, samplerScope: CoroutineScope) : IntentSampler(ctx, samplerScope) {

    var lastBssid : String = "invalid"

    private val wifiMan : WifiManager
    init{
        wifiMan = ctx.getSystemService(WIFI_SERVICE) as WifiManager
    }

    override val intentFilter: IntentFilter = IntentFilter(NETWORK_STATE_CHANGED_ACTION)
    override fun createLog(intent: Intent): SamplerLog? {
        val wifiInfo = wifiMan.connectionInfo
        wifiInfo?.let{wi ->
            Log.d(TAG, wi.supplicantState.toString())
            val newBssid = wi.bssid ?: "invalid"
            if(newBssid != lastBssid){
                lastBssid = newBssid
                return WifiRoamLog(SystemClock.elapsedRealtimeNanos(), newBssid)
            }
        }
        return null
    }
}