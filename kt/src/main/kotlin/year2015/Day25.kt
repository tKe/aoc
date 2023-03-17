package year2015

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day25 : PuzDSL({

    fun Pair<Int, Int>.cantorEncode() = second + (first + second).let { it * (it + 1) } / 2

    val codes = generateSequence(20151125L) { (it * 252533L) % 33554393L }

    part1 {
        input.split(' ', ',', '.')
            .mapNotNull(String::toIntOrNull)
            .let { (row, col) -> row - 1 to col - 1 } // offset to 0-based for cantor-encoding
            .cantorEncode()
            .let(codes::elementAt)
    }
})


fun main() = solveDay(
    25,
)
