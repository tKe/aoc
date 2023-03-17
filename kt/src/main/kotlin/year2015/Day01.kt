package year2015

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

@AoKSolution
object Day01 : PuzDSL({
    fun elevator(floor: Int, c: Char) = floor + when (c) {
        '(' -> 1
        ')' -> -1
        else -> 0
    }

    part1 { input.fold(0, ::elevator) }

    part2 { input.runningFold(0, ::elevator).indexOfFirst { it < 0 } }
})

fun main() = solveDay(1)
