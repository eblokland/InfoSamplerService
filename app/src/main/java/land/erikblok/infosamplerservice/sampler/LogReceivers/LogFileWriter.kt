package land.erikblok.infosamplerservice.sampler.LogReceivers

import android.util.Log
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog
import java.io.File
import java.io.FileWriter
import java.io.IOException

class LogFileWriter(outputFile: File) : BaseLogReceiver() {
    private val TAG: String = "LogFileWriter"
    var fileWriter: FileWriter;
    private var isClosed = false

    init {
        //Apparently the general pattern for init is to not catch exceptions and allow caller
        //to do it all
        if (!outputFile.exists()) outputFile.createNewFile()
        check(outputFile.canWrite())
        fileWriter = FileWriter(outputFile)
    }

    constructor(filePath: String) : this(File(filePath))

    override fun RecordLog(samplerLog: SamplerLog) {
        if(isClosed) return;
        fileWriter.write(samplerLog.toString());
    }

    override fun onDestroy(){
        fileWriter.close()
        isClosed = true
    }


}