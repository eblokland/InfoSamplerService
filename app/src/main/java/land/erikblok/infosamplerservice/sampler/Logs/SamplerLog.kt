package land.erikblok.infosamplerservice.sampler.Logs

open class SamplerLog(val timestamp: Long){
    override fun toString() : String{
        return "Timestamp: ${timestamp} "
    }
    open fun getData() : String = ""

    open fun getSimpleString() : String {
        return "$timestamp"
    }

    constructor() : this(-1){

    }

}
