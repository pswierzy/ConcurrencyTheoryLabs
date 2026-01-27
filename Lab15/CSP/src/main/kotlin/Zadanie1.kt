import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private suspend fun myproducer(ch1:SendChannel<Int>) {
    for(num in 1..100){
        delay(10)
        println("producent produkuje $num")
        ch1.send(num)
    }
    ch1.close()
}

private suspend fun przetwarzacz(chin:ReceiveChannel<Int>,chout:SendChannel<Int>, nb:Int){
    chin.consumeEach{
        delay(10)
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

    val c1=Channel<Int>(1)

    launch {myproducer(c1)}

    val N = 10

    var cin = c1
    var cout = Channel<Int>(1)

    for(num in 1..N) {
        val c1 = cin
        val c2 = cout
        launch {przetwarzacz(c1, c2 ,num)}
        cin = cout
        cout = Channel<Int>(1)
    }

    launch {konsument(cin)}

}