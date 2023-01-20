package land.erikblok.infosamplerservice.sampler.Logs

class CellularSignalStrengthLog(
    timestamp: Long, signalStrength: Int, override val signalName: String = "Cellular",
) : SignalStrengthLog(timestamp, signalStrength) {
}
