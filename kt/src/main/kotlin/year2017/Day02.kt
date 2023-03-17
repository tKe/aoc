package year2017

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution
import arrow.core.compose
import kotlin.math.absoluteValue

@AoKSolution
object Day02 : PuzDSL({
    part1 {
        lines.sumOf {
            val cells = it.split("\t").map(String::toInt)
            cells.max() - cells.min()
        }
    }

    part2 {
        lines.sumOf {
            val cells = it.split("\t").map(String::toInt).sorted()
            cells.asReversed().firstNotNullOf { a ->
                cells.filterNot(a::equals).firstNotNullOfOrNull { b ->
                    (a / b).takeIf { it * b == a }
                }
            }
        }
    }
})

fun main(): Unit = solveDay(2)
