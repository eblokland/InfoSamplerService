package land.erikblok.infosamplerservice.sampler.Logs

abstract class SamplerLog(val timestamp: Long){
    override fun toString() : String{
        return "Timestamp: ${timestamp} "
    }
    abstract fun getData() : String

}
