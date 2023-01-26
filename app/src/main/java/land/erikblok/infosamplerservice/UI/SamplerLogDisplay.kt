package land.erikblok.infosamplerservice.UI

import android.os.SystemClock
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import land.erikblok.infosamplerservice.sampler.Logs.CurrentLog
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog
import land.erikblok.infosamplerservice.sampler.Logs.VoltageLog

@Composable
fun Log(log : SamplerLog, modifier: Modifier = Modifier){
Surface(shape = MaterialTheme.shapes.medium, shadowElevation = 5.dp, modifier = Modifier.padding(5.dp)) {
    //Spacer(modifier = Modifier.width(10.dp).height(10.dp))
    Text(log.getData(), fontSize = 30.sp, style = MaterialTheme.typography.bodyMedium, modifier=modifier.padding(5.dp))
}
}

@Composable
fun FlowLog(flow : Flow<SamplerLog>, modifier : Modifier = Modifier, initialState : SamplerLog = SamplerLog()){
    val data = flow.collectAsState(initialState)
    Log(data.value, modifier)
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