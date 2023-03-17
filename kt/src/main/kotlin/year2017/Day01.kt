package year2017

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution
import arrow.core.compose
import kotlin.math.absoluteValue

@AoKSolution
object Day01 : PuzDSL({
    part1 {
        input.trim().let { it.asSequence() + it.first() }
            .zipWithNext { a, b -> if (a == b) a.digitToInt() else 0 }
            .sum()
    }

    part2 {
        input.trim().let {
            it.mapIndexed { index, c -> if (c == it[(index + (it.length / 2)) % it.length]) c.digitToInt() else 0 }
                .sum()
        }
    }
})

fun main(): Unit = solveDay(1)
