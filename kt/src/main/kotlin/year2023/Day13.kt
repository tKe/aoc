package year2023

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(
    13,
)

@AoKSolution
object Day13 : PuzDSL({
    fun List<String>.transposed() = first().indices.map {
        buildString { for (line in this@transposed) append(line[it]) }
    }

    fun List<String>.reflection() = zipWithNext(String::equals)
        .mapIndexedNotNull { idx, b -> idx.takeIf { b }?.inc() }
        .firstOrNull { lines ->
            take(lines).reversed().zip(drop(lines)) { a, b -> a == b }.all { it }
        } ?: 0

    part1 {
        val grids = input.split("\n\n").map { it.lines() }
        grids.sumOf {
            100 * it.reflection() + it.transposed().reflection()
        }
    }

    fun String.smudges(other: String) = zip(other) { a, b -> a != b }.count { it }
    fun List<String>.smudgeReflection() = zipWithNext(String::smudges)
        .mapIndexedNotNull { idx, i -> idx.takeIf { i <= 1 }?.inc() }
        .firstOrNull { lines ->
            take(lines).reversed().zip(drop(lines), String::smudges).sum() == 1
        } ?: 0


    part2 {
        val grids = input.split("\n\n").map { it.lines() }
        grids.sumOf {
            100 * it.smudgeReflection() + it.transposed().smudgeReflection()
        }
    }
})
