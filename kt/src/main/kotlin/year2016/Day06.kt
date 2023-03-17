package year2016

import aok.PuzDSL
import aoksp.AoKSolution
import arrow.core.Either
import arrow.core.mapOrAccumulate
import arrow.core.separateEither

@AoKSolution
object Day06 : PuzDSL({
    fun List<String>.transpose(): Sequence<Sequence<Char>> = first().indices.asSequence().map { index ->
        asSequence().map { it[index] }
    }

    fun List<String>.decodeSignal(leastCommon: Boolean = false) = transpose().map {
        it.groupingBy { c -> c }.eachCount()
            .run { if (leastCommon) minBy { (_, count) -> count } else maxBy { (_, count) -> count } }
            .key
    }.joinToString("")

    part1 { lines.decodeSignal()
    }

    part2 { lines.decodeSignal(leastCommon = true) }
})

fun main() = solveDay(
    6,
//    input = InputProvider.raw(
//        """
//        eedadn
//        drvtee
//        eandsr
//        raavrd
//        atevrs
//        tsrnev
//        sdttsa
//        rasrtv
//        nssdts
//        ntnada
//        svetve
//        tesnvt
//        vntsnd
//        vrdear
//        dvrsen
//        enarar
//    """.trimIndent()
//    )
)
