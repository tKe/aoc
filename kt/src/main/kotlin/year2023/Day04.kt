package year2023

import aok.PuzDSL
import aoksp.AoKSolution
import utils.splitIntsNotNull

fun main() = solveDay(
    4,
//    input = aok.InputProvider.Example
)

@AoKSolution
object Day04 : PuzDSL({
    val parseWins = lineParser { line ->
        line.split(" | ", limit = 2)
            .map { it.splitIntsNotNull(" ").toSet() }
            .let { (winners, numbers) -> numbers.count(winners::contains) }
    }

    part1(parseWins) { wins ->
        wins.filter { it > 0 }
            .sumOf { 1.shl(it - 1) }
    }

    part2(parseWins) { wins ->
        val copies = IntArray(wins.size) { 1 }
        for (idx in wins.indices) repeat(wins[idx]) { copies[it + idx + 1] += copies[idx] }
        copies.sum()
    }
})
