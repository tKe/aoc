package year2023

import aok.PuzDSL
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds

fun main() = solveDay(
    9,
    warmup = aok.Warmup.eachFor(3.seconds), runs = 1_000,
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

@AoKSolution
object Day09Array : PuzDSL({
    fun IntArray.next(): Int {
        var next = 0
        for (i in lastIndex downTo 1) {
            next += this[i]
            for (j in 0..<i)
                this[j] = this[j + 1] - this[j]
        }
        return next
    }

    val parser = lineParser {
        it.split(' ').mapNotNull(String::toIntOrNull).toIntArray()
    }

    part1(parser) { histories -> histories.sumOf { it.next() } }
    part2(parser) { histories ->
        histories.sumOf {
            it.reverse()
            it.next()
        }
    }
})

@AoKSolution
object Day09Maths : PuzDSL({
    // purloined from @ephemient
    fun List<Int>.next(): Int {
        var c = 1
        var s = 0
        for ((i, x) in withIndex()) {
            s = c * x - s
            c = c * (size - i) / (i + 1)
        }
        return s
    }

    val parser = lineParser {
        it.split(' ').mapNotNull(String::toIntOrNull)
    }

    part1(parser) { histories -> histories.sumOf { it.next() } }
    part2(parser) { histories -> histories.sumOf { it.asReversed().next() } }
})
