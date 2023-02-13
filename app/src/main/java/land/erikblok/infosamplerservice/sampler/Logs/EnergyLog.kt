package land.erikblok.infosamplerservice.sampler.Logs


class EnergyLog(timestamp: Long, val energy : Long) : SamplerLog(timestamp) {

    constructor(): this(-1, -1)
    override fun toString(): String {
        return "${super.toString()} ${getData()}"
    }

    override fun getData(): String {
        return "Battery energy: $energy"
    }

    override fun getSimpleString(): String {
        return "${super.getSimpleString()} energy $energy"
    }
}