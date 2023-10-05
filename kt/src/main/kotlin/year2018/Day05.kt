package year2018

import aok.PuzDSL
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds
@AoKSolution
object Day05 : PuzDSL({
    fun Char.toggleCase() = plus(if (this !in 'a'..'z') 32 else -32)
    infix fun Char.reactsTo(other: Char) = equals(other.toggleCase())

    fun CharSequence.react(vararg ignore: Char) = ArrayDeque<Char>(length).apply {
        for (c in this@react) if (c !in ignore) {
            if (size > 0 && last() reactsTo c) removeLast()
            else add(c)
        }
    }.size

    part1 { input.react() }
    part2 { ('a'..'z').minOf { c -> input.react(c, c.toggleCase()) } }
})

fun main(): Unit = solveDay(
    5,
    warmup = aok.Warmup.eachFor(2.seconds),
    runs = 100,
//    input = aok.InputProvider.raw("dabAcCaCBAcCcaDA"),
)
