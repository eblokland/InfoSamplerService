package land.erikblok.infosamplerservice.sampler.Logs

class WifiSignalStrengthLog(
    timestamp: Long,
    signalStrength: Int,
) : SignalStrengthLog(timestamp, signalStrength) {
    override val signalName: String = "Wifi"
}