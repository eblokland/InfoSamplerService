package land.erikblok.infosamplerservice.sampler.LogReceivers

import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog

/**
 * Base class for log receivers, that will be passed to loggers in order to record their output
 * either to a log file or somewhere else (debug display on-screen?)
 */
abstract class BaseLogReceiver {
    abstract fun RecordLog(samplerLog: SamplerLog)
    abstract fun onDestroy()
}