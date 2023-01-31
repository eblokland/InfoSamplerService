package land.erikblok.infosamplerservice.sampler.Logs

class DisplayBrightnessLog(timestamp: Long, val brightness : Int) : SamplerLog(timestamp) {
    override fun toString(): String {
        return "${super.toString()} , ${getData()}"
    }

    override fun getData(): String {
        return "Display brightness : $brightness"
    }

    override fun getSimpleString(): String {
        return "${super.getSimpleString()} dispBrightness $brightness"
    }
}