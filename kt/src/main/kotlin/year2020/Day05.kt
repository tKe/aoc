package year2020

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(
    5,
//    input = aok.InputProvider.raw(
//        """
//        BFFFBBFRRR
//        FFFBBBFRRR
//        BBFFBBFRLL
//    """.trimIndent()
//    )
)

@AoKSolution
object Day05 : PuzDSL({

    val parser = lineParser {
        fun String.toBin(t: Char) = map { if(it == t) 1 else 0 }.joinToString("").toInt(2)
        it.substring(0, 7).toBin('B')* 8 + it.substring(7).toBin('R')
    }

    part1(parser) { seatIds -> seatIds.max() }

    part2(parser) { seats ->
        seats.asSequence()
            .sorted().zipWithNext { a, b -> (a + 1).takeUnless(b::equals) }
            .filterNotNull().first()
    }
})

