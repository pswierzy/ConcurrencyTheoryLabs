import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withTimeoutOrNull



private suspend fun trySecond(channel: Channel<Int>):Boolean {
    val token = withTimeoutOrNull(50) { channel.receive() }
    return token != null
}
private suspend fun eat(id: Int, ch1: Channel<Int>, ch2: Channel<Int>) {
    println(">>> FILOZOF $id JE <<<")
    delay((100..300).random().toLong())
    println("Filozof $id odkłada widelce")

    ch1.send(1)
    ch2.send(1)
}

private suspend fun filozof(ch1: Channel<Int>, ch2: Channel<Int>, num: Int) {
    while (true) {
        println("Filozof $num myśli...")
        delay((100..300).random().toLong())

        select<Unit> {
            ch1.onReceive {
                println("Filozof $num ma lewy. Szuka prawego...")
                if (!trySecond(ch2)) {
                    println("Nie udało się! Filozof $num oddaje lewy.")
                    ch1.send(1)
                } else {
                    eat(num, ch1, ch2)
                }
            }

            ch2.onReceive {
                println("Filozof $num ma prawy. Szuka lewego...")
                if (!trySecond(ch1)) {
                    println("Nie udało się! Filozof $num oddaje prawy.")
                    ch2.send(1)
                } else {
                    eat(num, ch1, ch2)
                }
            }
        }
    }
}

fun main() = runBlocking {
    val widelce = List(5) {Channel<Int>(1)}
    widelce.forEach { it.send(1) }

    val filo = List(5) {id ->
        val left = widelce[id]
        val right = widelce[(id+1)%5]

        launch{ filozof(left, right, id) }
    }

    delay(5000)
    coroutineContext.cancelChildren()
}