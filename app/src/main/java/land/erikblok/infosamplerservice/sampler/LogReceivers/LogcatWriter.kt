package land.erikblok.infosamplerservice.sampler.LogReceivers

import android.util.Log
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog

class LogcatWriter : BaseLogReceiver() {
    private val TAG = "SAMPLER_LOGGER"
    override fun RecordLog(samplerLog: SamplerLog) {
        Log.i(TAG, samplerLog.toString())
    }

    override fun onDestroy() {

    }
}