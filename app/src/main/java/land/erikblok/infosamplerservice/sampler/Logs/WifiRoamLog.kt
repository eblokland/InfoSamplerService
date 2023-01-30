package land.erikblok.infosamplerservice.sampler.Logs

class WifiRoamLog(timestamp: Long, val bssid : String) : SamplerLog(timestamp) {
    override fun toString(): String {
        return "${super.toString()} roamed to AP $bssid"
    }

    override fun getData(): String {
        return bssid
    }
}