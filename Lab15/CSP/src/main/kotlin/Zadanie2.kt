import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select

private suspend fun producent(channels: List<SendChannel<Int>>) {
    for (num in 1..100) {
        delay(10)
        select<Unit> {
            channels.forEach {
                ch -> ch.onSend(num){}
            }
        }
    }
    channels.forEach{
        ch -> ch.close()
    }
}

private suspend fun posrednik(chin:ReceiveChannel<Int>, chout:SendChannel<Int>, nb:Int){
    chin.consumeEach{
        delay(100)
        println("przetwarzacz $nb przetwarza $it")
        chout.send(it)
        if(it==100){
            chout.close()
        }
    }
}

private suspend fun konsument(chin:ReceiveChannel<Int>){
    chin.consumeEach{
        println("Konsument odbiera $it")
    }
}

fun main()=runBlocking<Unit>{

    val N = 4
    val inChannels = mutableListOf<Channel<Int>>()
    val outChannel = Channel<Int>()

    for(num in 1..N) {
        val chin = Channel<Int>()
        inChannels.add(chin)
        launch{posrednik(chin, outChannel, num)}
    }

    launch {producent(inChannels)}
    launch {konsument(outChannel)}

}