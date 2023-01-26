package land.erikblok.infosamplerservice.sampler.Logs

class CellularSignalStrengthLog(
    timestamp: Long, signalStrength: Int
) : SignalStrengthLog(timestamp, signalStrength) {
    constructor() : this(-1, -1, )
    override val signalName: String = "Cellular"
}
