package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import android.content.Context.DISPLAY_SERVICE
import android.hardware.display.DisplayManager
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import land.erikblok.infosamplerservice.sampler.Logs.DisplayStateLog
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog

private val TAG = "DMSAMPLER"

class DisplayManagerSampler(ctx: Context, samplerScope: CoroutineScope) : BaseSampler(
    ctx,
    samplerScope
) {

    private val stateMap : HashMap<Int, Int> = HashMap()

    init{
        //set up initial stateMap of each display (it'll probably just be 1 display,
        //but i'm being robust here)
        val dm = ctx.getSystemService(DISPLAY_SERVICE) as DisplayManager
        dm.displays.forEach { stateMap[it.displayId] = it.state }
    }

    override val logSource: Flow<SamplerLog> = callbackFlow {
        val dm = ctx.getSystemService(DISPLAY_SERVICE) as DisplayManager
        var prevState = -1
        val listener = object : DisplayManager.DisplayListener {
            override fun onDisplayAdded(displayId: Int) {
                stateMap[displayId] = dm.getDisplay(displayId).state
            }

            override fun onDisplayRemoved(displayId: Int) {
                stateMap.remove(displayId)
            }

            override fun onDisplayChanged(displayId: Int) {
                val state = dm.getDisplay(displayId).state
                //this event fires for more than just display state, don't
                //send the log if the state hasn't changed.
                if(state != stateMap[displayId]) {
                    stateMap[displayId] = state
                    trySendBlocking(
                        DisplayStateLog(
                            SystemClock.elapsedRealtimeNanos(),
                            state,
                            displayId
                        )
                    ).onFailure { Log.d(TAG, "Failed to send displayman log") }
                }
            }

        }
        dm.registerDisplayListener(listener, null)
        //send initial state
        dm.displays.forEach {
            trySendBlocking(
                DisplayStateLog(
                    SystemClock.elapsedRealtimeNanos(),
                    it.state,
                    it.displayId
                )
            )
        }
        awaitClose { dm.unregisterDisplayListener(listener) }
    }
}