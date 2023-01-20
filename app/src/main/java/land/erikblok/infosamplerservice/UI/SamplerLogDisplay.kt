package land.erikblok.infosamplerservice.UI

import android.os.SystemClock
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import land.erikblok.infosamplerservice.sampler.Logs.CurrentLog
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog
import land.erikblok.infosamplerservice.sampler.Logs.VoltageLog

@Composable
fun Log(log : SamplerLog){
    Text(log.getData(), fontSize = 30.sp)
}

@Preview
@Composable
fun previewLog(){
    Column {
        Log(VoltageLog(0, 430000))
        Spacer(modifier = Modifier.height(4.dp))
        Log(CurrentLog(0, 1111))
    }
}