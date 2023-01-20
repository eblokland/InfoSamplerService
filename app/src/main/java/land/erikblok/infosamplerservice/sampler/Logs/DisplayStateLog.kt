package land.erikblok.infosamplerservice.sampler.Logs

import android.view.Display

class DisplayStateLog(timestamp: Long, private val state : Int, private val displayId : Int) : SamplerLog(timestamp) {

    override fun toString(): String {
        return "${super.toString()}, display $displayId state ${stateToString(state)}"
    }

    private fun stateToString(state:Int) : String{
        return when (state) {
            Display.STATE_ON -> "on"
            Display.STATE_VR -> "vr"
            Display.STATE_DOZE -> "doze"
            Display.STATE_OFF -> "off"
            Display.STATE_UNKNOWN -> "unknown"
            else -> "invalid"
        }
    }
}