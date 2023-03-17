package year2016

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

@AoKSolution
object Day20 : PuzDSL({
    fun PuzzleInput.parseRanges() = lines.asSequence()
        .map {
            val (start, end) = it.split("-").map(String::toUInt)
            start..end
        }

    fun Sequence<UIntRange>.merged() = sequence {
        var current: UIntRange? = null
        for (range in sortedBy(UIntRange::first)) {
            when {
                current == null -> current = range
                range.first in current || range.first == current.last.inc() ->
                    current = current.first..maxOf(current.last, range.last)

                else -> yield(current).also { current = range }
            }
        }
        current?.let { yield(it) }
    }

    fun UIntRange.size() = 1u + (last - first)

    part1 {
        parseRanges().merged().first().last.inc()
    }

    part2 {
        // use unsigned rollover for our benefits. UInt.MAX_VALUE+1u == 0u possible values under UInt arithmetic.
        parseRanges().merged().fold(0u) { ips, block -> ips - block.size() }
    }
})

fun main() = solveDay(
    20,
//    input = InputProvider.raw(
//        """
//        30-60
//        40-45
//        0-10
//        5-29
//    """.trimIndent()
//    )
)
