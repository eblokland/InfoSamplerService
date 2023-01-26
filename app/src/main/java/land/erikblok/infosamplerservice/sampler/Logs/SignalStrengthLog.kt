package land.erikblok.infosamplerservice.sampler.Logs



abstract class SignalStrengthLog(timestamp: Long, val signalStrength : Int) : SamplerLog(timestamp){
    constructor() : this(-1, -1)
    abstract val signalName : String
    override fun toString(): String {
        return "${super.toString()} Signal strength of ${signalName} now ${signalStrength}"
    }

    override fun getData(): String {
        return "$signalName strength: $signalStrength"
    }

}
