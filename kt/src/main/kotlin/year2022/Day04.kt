package year2022

import PuzzleDefinition
import solveAll

fun main() = solveAll<Day04DSL>()
sealed class Day04DSL(variant: String? = null, body: PuzzleDefinition<Int, Int>) : Puz22DSL<Int, Int>(4, variant, body)


object Day04 : Day04DSL(body = {
    infix fun IntRange.overlaps(other: IntRange) =
        first <= other.last && other.first <= last

    infix operator fun IntRange.contains(other: IntRange) =
        all(other::contains)

    fun List<String>.process(predicate: (IntRange, IntRange) -> Boolean) = count {
        val (a, b, c, d) = it.split('-', ',')
        predicate(a.toInt()..b.toInt(), c.toInt()..d.toInt())
    }

    part1 {
        lines.process { a, b -> a in b || b in a }
    }

    part2 {
        lines.process { a, b -> a overlaps b }
    }
})

object Day04Inequalities : Day04DSL(body = {
    fun List<String>.count(predicate: (a: Int, b: Int, c: Int, d: Int) -> Boolean) = count { it: String ->
        val (a, b, c, d) = it.split('-', ',')
        predicate(a.toInt(), b.toInt(), c.toInt(), d.toInt())
    }
    part1 { lines.count { a, b, c, d -> a >= c && d >= b || c >= a && b >= d } }
    part2 { lines.count { a, b, c, d -> a <= d && c <= b } }
})
