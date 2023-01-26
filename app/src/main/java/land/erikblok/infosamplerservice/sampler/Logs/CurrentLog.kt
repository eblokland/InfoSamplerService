package land.erikblok.infosamplerservice.sampler.Logs

class CurrentLog(timestamp: Long, val current : Int) : SamplerLog(timestamp) {
    constructor(): this(-1, -1)
    override fun toString(): String {
        return "${super.toString()} ${getData()}"
    }

    override fun getData(): String {
        return "Battery current: $current"
    }
}