package land.erikblok.infosamplerservice.sampler.Samplers

import android.content.Context
import android.os.SystemClock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

//For now set initial sample rate to 1ms for estimation purposes.
val initialSampleRate: Long = 1
val numSamplesToAverage: Int = 4; //magic number :)

/**
 * Finds the total time in ms between two sample updates.
 * May have error up to 1ms
 */
suspend fun <T> estimateUpdateRateOneshot(
    f: () -> T,
    disp: CoroutineDispatcher = Dispatchers.Default
): Long = withContext(disp) {
    var output: T
    var newOutput = f()
    var changeoverTime: Long = -1
    var beginTime = SystemClock.elapsedRealtime()
    do {
        output = newOutput
        delay(initialSampleRate)
        newOutput = f()

        //exit condition for emulator, where this will never change!
        if((SystemClock.elapsedRealtime() - beginTime > 2000)) return@withContext 2000
    } while (newOutput == output)
    changeoverTime = SystemClock.elapsedRealtime();
    do {
        output = newOutput
        delay(initialSampleRate)
        newOutput = f()
    } while (newOutput == output)
    return@withContext SystemClock.elapsedRealtime() - changeoverTime;
}

suspend fun <T> estimateUpdateRateAvg(
    f: () -> T,
    numSamples: Int = numSamplesToAverage,
    disp: CoroutineDispatcher = Dispatchers.Default
): Long =
    withContext(disp) {
        var total: Long = 0
        for (i in 1..numSamples) {
            total += estimateUpdateRateOneshot(f, disp)
        }
        return@withContext total / numSamples
    }