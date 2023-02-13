package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import land.erikblok.infosamplerservice.sampler.Logs.DisplayBrightnessLog
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog

private val TAG = "BRIGHTNESS_SAMPLER"

class DisplayBrightnessSampler(ctx: Context, samplerScope: CoroutineScope) : BaseSampler(ctx,
    samplerScope
) {

    val settingString = Settings.System.SCREEN_BRIGHTNESS
    val settingFun : () -> Int = { Settings.System.getInt(ctx.contentResolver, settingString)}

    override val logSource: Flow<SamplerLog> = callbackFlow{
        val co = object : ContentObserver(Handler(Looper.getMainLooper())){
            override fun onChange(selfchange: Boolean){
                val newBrightness = settingFun()
                trySendBlocking(
                    DisplayBrightnessLog(SystemClock.uptimeMillis(), newBrightness)
                ).onFailure { Log.d(TAG, "failed to send brightness sample") }
            }
        }
        ctx.contentResolver.registerContentObserver(Settings.System.getUriFor(settingString), false, co)
        //send initial state
        trySendBlocking(DisplayBrightnessLog(SystemClock.uptimeMillis(), settingFun()))

        awaitClose{
            ctx.contentResolver.unregisterContentObserver(co)
        }
    }
}