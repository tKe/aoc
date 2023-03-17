package year2016

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day15 : PuzDSL({
    data class Disc(val positions: Int, val start: Int)

    fun Disc.positionAt(n: Int) = (start + n).mod(positions)
    fun List<Disc>.passableAt(n: Int) = asSequence().withIndex()
        .all { (idx, disc) -> disc.positionAt(n + 1 + idx) == 0 }

    fun String.parseDisc() = split(' ', '.').mapNotNull(String::toIntOrNull)
        .let { (positions, start) -> Disc(positions, start) }

    fun List<Disc>.firstPassableTime() = generateSequence(0, Int::inc).first(::passableAt)

    part1 {
        lines.map(String::parseDisc).firstPassableTime()
    }
    part2 {
        lines.map(String::parseDisc).plus(Disc(11, 0)).firstPassableTime()
    }
})

fun main() = solveDay(
    15,
//    input = InputProvider.raw(
//        """
//        Disc #1 has 5 positions; at time=0, it is at position 4.
//        Disc #2 has 2 positions; at time=0, it is at position 1.
//    """.trimIndent()
//    )
)
