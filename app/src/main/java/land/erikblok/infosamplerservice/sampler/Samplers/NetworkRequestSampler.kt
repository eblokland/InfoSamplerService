package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Context.WIFI_SERVICE
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog
import land.erikblok.infosamplerservice.sampler.Logs.WifiRoamLog

private val TAG = "NETWORK_REQ_SAMPLER"


class NetworkRequestSampler(ctx: Context, samplerScope: CoroutineScope) : BaseSampler(ctx,
    samplerScope
) {

    //this works on >12, would work on >10 except google didn't implement the api correctly
    override val logSource: Flow<SamplerLog> = callbackFlow{
        val builder = NetworkRequest.Builder()
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        val conMan = ctx.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiman = ctx.getSystemService(WIFI_SERVICE) as WifiManager
        val nr = builder.build()
        val nc = object : NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                Log.i(TAG, "entered callback")
                val wifiInfo = networkCapabilities.transportInfo
                if(wifiInfo is WifiInfo){
                    Log.i(TAG, "???")
                    trySendBlocking(
                        WifiRoamLog(SystemClock.elapsedRealtimeNanos(), wifiInfo.bssid)
                    ).onFailure { Log.d(TAG,"Failed to send wifi roam log") }
                }
                else{
                    Log.i(TAG, "${networkCapabilities} $wifiInfo ${wifiman.connectionInfo.bssid}")
                }
            }
        }

        conMan.registerNetworkCallback(nr, nc)

        awaitClose{
            conMan.unregisterNetworkCallback(nc)
        }
    }

}