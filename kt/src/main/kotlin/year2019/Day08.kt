package year2019

import aok.PuzDSL
import aoksp.AoKSolution

fun main(): Unit = solveDay(
    8,
)

@AoKSolution
object Day08 : PuzDSL({
    part1 {
        input.chunked(25 * 6).minBy { it.count('0'::equals) }.let {
            it.count('1'::equals) * it.count('2'::equals)
        }
    }

    part2 {
        val layers = input.chunked(25 * 6)
        repeat(25 * 6) { i ->
            when (layers.firstNotNullOf { it[i].takeUnless('2'::equals) }) {
                '0' -> print("⚫")
                '1' -> print("⚪")
            }
            if (i % 25 == 24) println()
        }
    }
})

