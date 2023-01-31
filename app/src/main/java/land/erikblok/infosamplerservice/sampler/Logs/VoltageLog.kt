package land.erikblok.infosamplerservice.sampler.Logs

class VoltageLog(timestamp: Long, val voltage : Int) : SamplerLog(timestamp) {
    constructor() : this(-1, -1)
    override fun toString(): String {
        return "${super.toString()} ${getData()}"
    }

    override fun getSimpleString(): String {
        return "${super.getSimpleString()} Voltage $voltage"
    }

    override fun getData(): String {
        return "Battery Voltage: $voltage"
    }
}