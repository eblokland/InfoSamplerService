package land.erikblok.infosamplerservice.sampler.Logs

class VoltageLog(timestamp: Long, val voltage : Int) : SamplerLog(timestamp) {
    override fun toString(): String {
        return "${super.toString()} Battery Voltage: ${voltage}"
    }
}