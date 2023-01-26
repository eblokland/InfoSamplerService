package land.erikblok.infosamplerservice.sampler.Logs

open class SamplerLog(val timestamp: Long){
    override fun toString() : String{
        return "Timestamp: ${timestamp} "
    }
    open fun getData() : String = ""

    constructor() : this(-1){

    }

}
