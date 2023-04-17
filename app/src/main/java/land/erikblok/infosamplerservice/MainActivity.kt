package land.erikblok.infosamplerservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import land.erikblok.infosamplerservice.UI.FlowLog
import land.erikblok.infosamplerservice.UI.Log
import land.erikblok.infosamplerservice.sampler.Logs.CurrentLog
import land.erikblok.infosamplerservice.sampler.Logs.SamplerLog
import land.erikblok.infosamplerservice.sampler.Logs.VoltageLog
import land.erikblok.infosamplerservice.ui.theme.InfoSamplerServiceTheme


class MainActivity : ComponentActivity() {
    private lateinit var sampler : EnvironmentSampler
    private var serviceBound : Boolean = false
    private var statsList : SnapshotStateList<SharedFlow<SamplerLog>> = ArrayList<SharedFlow<SamplerLog>>().toMutableStateList()

    private val serviceConn = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as EnvironmentSampler.SamplerBinder
            sampler = binder.getService()
            serviceBound = true

            sampler.getFlows().forEach { statsList.add(it) }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            statsList.clear()
            serviceBound = false
        }

        override fun onBindingDied(name: ComponentName?) {
            statsList.clear()
            super.onBindingDied(name)
        }

        override fun onNullBinding(name: ComponentName?) {
            statsList.clear()
            super.onNullBinding(name)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainLayout(statsList)
        }
    }

    override fun onStart(){
        super.onStart()
        Intent(this, EnvironmentSampler::class.java).also {
            intent -> bindService(intent, serviceConn, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        statsList.clear()
        unbindService(serviceConn)
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun MainLayout(statsList : List<SharedFlow<SamplerLog>>, ctx: Context? = null){
    fun stopService(){
        ctx?.startService(Intent(
            ctx,
            EnvironmentSampler::class.java,
        ).apply{
            this.action = ACTION_STOPLOGCAT
        })
        ctx?.startService(Intent(
            ctx,
            EnvironmentSampler::class.java,
        ).apply{
            this.action = ACTION_STOPFILE
        })
    }


    InfoSamplerServiceTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column() {
                Button(
                    onClick = { stopService() },
                    content = { Text("Stop running logs") },
                )
                Spacer(modifier = Modifier.height(5.dp))
                    flowList(statsList)
            }
        }
    }
}

@Composable
fun statList(stats : List<SamplerLog>){
    LazyColumn{
        items(stats) {
            Log(it)
        }
    }
}
@Composable
fun flowList(stats : List<SharedFlow<SamplerLog>>){
    LazyColumn{
        items(stats){
            FlowLog(it)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    //MainLayout()
}