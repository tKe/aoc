package year2017

import aok.PuzDSL
import aok.Warmup
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day13 : PuzDSL({
    val parseRanges = parser {
        lines.associate {
            val (depth, range) = it.split(": ")
            depth.toInt() to range.toInt()
        }
    }

    fun caughtAt(time: Int, range: Int) = range > 0 && time.mod(2 * range - 2) == 0

    part1 {
        parseRanges().entries
            .sumOf { (depth, range) -> if (caughtAt(depth, range)) depth * range else 0 }
    }

    part2 {
        val rangeMap = parseRanges()

        generateSequence(0, Int::inc).first { delay ->
            rangeMap.none { (depth, range) -> caughtAt(depth + delay, range) }
        }
    }
})


fun main(): Unit = solveDay(
    13,
    warmup = Warmup.eachFor(5.seconds), runs = 30,
//    input = InputProvider.raw(
//        """
//        0: 3
//        1: 2
//        4: 4
//        6: 4
//    """.trimIndent()
//    )
)
