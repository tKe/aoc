package year2018

import aok.PuzDSL
import aoksp.AoKSolution
import kotlin.math.absoluteValue

fun main(): Unit = solveDay(
        9,
//        warmup = eachFor(3.seconds), runs = 20,
//        input = aok.InputProvider.raw("13 players; last marble is worth 7999 points"),
)

@AoKSolution
object Day09 : PuzDSL({
    val parse = parser { input.split(" ").mapNotNull(String::toIntOrNull) }

    fun highScore(players: Int, lastMarble: Int) = Circle(0, capacity = lastMarble).run {
        val scores = LongArray(players)
        for (marble in 1..lastMarble) {
            val score = play(marble)
            if (score > 0L) scores[marble % players] += score
        }
        scores.max()
    }

    part1(parse) { (players, lastMarble) ->
        highScore(players, lastMarble)
    }

    part2(parse) { (players, lastMarble) ->
        highScore(players, lastMarble * 100)
    }
}) {
    @JvmInline
    value class Circle(private val marbles: ArrayDeque<Int>) {
        constructor(vararg marbles: Int, capacity: Int = 1000)
                : this(ArrayDeque<Int>(capacity).also { it += marbles.asIterable() })

        private fun rotate(n: Int) = when {
            n > 0 -> repeat(n) { marbles.addFirst(marbles.removeLast()) }
            else -> repeat(n.absoluteValue) { marbles.addLast(marbles.removeFirst()) }
        }

        fun play(marble: Int) = 0L + if (marble % 23 == 0) {
            rotate(-6)
            marble + marbles.removeAt(1)
        } else {
            rotate(1)
            marbles.addFirst(marble)
            0
        }

    }
}

