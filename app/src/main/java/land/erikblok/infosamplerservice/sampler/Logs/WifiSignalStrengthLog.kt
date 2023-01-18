package land.erikblok.infosamplerservice.sampler.Logs

class WifiSignalStrengthLog(
    timestamp: Long,
    signalStrength: Int,
    override val signalName: String = "Wifi"
) : SignalStrengthLog(timestamp, signalStrength) {
}