package land.erikblok.infosamplerservice.sampler.Logs

class CurrentLog(timestamp: Long, val current : Int) : SamplerLog(timestamp) {
    override fun toString(): String {
        return "${super.toString()} Battery current: $current"
    }
}