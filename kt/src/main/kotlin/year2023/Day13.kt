package year2023

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(
    13,
    warmup = aok.Warmup.iterations(300), runs = 10
)

@AoKSolution
object Day13 : PuzDSL({
    val parser = parser { input.split("\n\n").map { it.lines() } }

    fun List<String>.transposed() = first().indices.map {
        buildString { for (line in this@transposed) append(line[it]) }
    }

    fun List<String>.reflection() = (1..lastIndex).firstOrNull { lines ->
        take(lines).reversed().zip(drop(lines)) { a, b -> a == b }.all { it }
    } ?: 0

    fun List<List<String>>.summarizeBy(f: List<String>.() -> Int) = sumOf { 100 * it.f() + it.transposed().f() }

    part1(parser) { it.summarizeBy { reflection() } }

    fun String.smudges(other: String) = zip(other) { a, b -> a != b }.count { it }
    fun List<String>.smudgeReflection() = (1..lastIndex).firstOrNull { lines ->
        take(lines).reversed().zip(drop(lines), String::smudges).sum() == 1
    } ?: 0

    part2(parser) { it.summarizeBy { smudgeReflection() } }
})
