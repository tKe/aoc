package year2020

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(
    6,
)

@AoKSolution
object Day06 : PuzDSL({
    fun String.toAlphaInt() = fold(0) { acc, c -> acc.or(1.shl(c - 'a')) }

    val parser = parser {
        input.split("\n\n")
            .map {it.lineSequence().map(String::toAlphaInt) }
    }

    part1(parser) { groups ->
        groups.sumOf { it.reduce(Int::or).countOneBits() }
    }
    part2(parser) { groups ->
        groups.sumOf { it.reduce(Int::and).countOneBits() }
    }
})

