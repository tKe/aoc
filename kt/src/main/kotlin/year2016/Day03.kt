package year2016

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day03 : PuzDSL({
    fun validTriangle(a: Int, b: Int, c: Int) = maxOf(a, b, c).let { it < (a + b + c - it) }

    part1 {
        lines.map { it.split(" ").mapNotNull(String::toIntOrNull) }
            .count { (a, b, c) -> validTriangle(a, b, c) }
    }

    part2 {
        lines.map { it.split(" ").mapNotNull(String::toIntOrNull) }
            .chunked(3) { (r1, r2, r3) ->
                (0 until 3).count {
                    validTriangle(r1[it], r2[it], r3[it])
                }
            }
            .sum()
    }
})


fun main() = solveDay(
    3,
)
