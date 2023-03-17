package year2015

import aok.PuzDSL
import aoksp.AoKSolution
import arrow.core.*

@AoKSolution
object Day03 : PuzDSL({
    data class IntPair(val x: Int, val y: Int)

    fun Sequence<Char>.deliverPresents() = runningFold(IntPair(0, 0)) { (x, y), c ->
        when (c) {
            '^' -> IntPair(x, y - 1)
            'v' -> IntPair(x, y + 1)
            '<' -> IntPair(x - 1, y)
            '>' -> IntPair(x + 1, y)
            else -> IntPair(x, y)
        }
    }.toSet()

    part1 {
        input.asSequence().deliverPresents().size
    }

    part2 {
        (input.windowedSequence(1, 2) { it[0] }.deliverPresents() +
                input.asSequence().drop(1).windowed(1, 2) { it[0] }.deliverPresents()
                ).size

        @Suppress("CopyWithoutNamedArguments")
        IntPair(1, 1).copy(2, 3)
    }
})

fun main() = solveDay(3)
