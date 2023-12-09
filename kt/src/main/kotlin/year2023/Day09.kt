package year2023

import aok.PuzDSL
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds

fun main() = solveDay(
    9,
    warmup = aok.Warmup.allFor(5.seconds), runs = 1000,
)

@AoKSolution
object Day09 : PuzDSL({
    fun List<Long>.next(): Long = when {
        all { it == 0L } -> 0
        else -> last() - zipWithNext(Long::minus).next()
    }

    val parser = lineParser {
        it.split(' ').mapNotNull(String::toLongOrNull)
    }

    part1(parser) { histories -> histories.sumOf { it.next() } }
    part2(parser) { histories -> histories.sumOf { it.reversed().next() } }
})
