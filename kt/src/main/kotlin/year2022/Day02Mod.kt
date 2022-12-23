package year2022

import InputScope
import aoksp.AoKSolution
import solveAll

private fun scorePart1(ca: Char, cx: Char): Int {
    val a = ca - 'A'
    val x = cx - 'X'
    return 1 + x + 3 * ((4 + x - a) % 3)
}

private fun scorePart2(ca: Char, cx: Char): Int {
    val a = ca - 'A'
    val x = cx - 'X'
    return 1 + (a + x + 2) % 3 + 3 * x
}

@AoKSolution
object Day02Mod {
    private inline fun List<String>.process(score: (theirs: Char, Char) -> Int) =
        sumOf { if (it.isNotBlank()) score(it[0], it[2]) else 0 }

    context(InputScope) fun part1() = lines.process(::scorePart1)
    context(InputScope) fun part2() = lines.process(::scorePart2)
}

fun main(): Unit = solveAll(day = 2)
