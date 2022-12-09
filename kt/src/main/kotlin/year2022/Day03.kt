package year2022

import PuzzleDefinition
import solveAll

fun main() = repeat(1) { solveAll<Day03DSL>(runIterations = 1) }
sealed class Day03DSL(variant: String? = null, body: PuzzleDefinition<Int, Int>) : Puz22DSL<Int, Int>(3, variant, body)

fun Char.toPriority() = this - if (this >= 'a') 'a' - 1 else 'A' - 27
object Day03Sets : Day03DSL(body = {
    part1 {
        lines.map { it.chunked(it.length / 2, CharSequence::toSet) }
            .sumOf { (a, b) -> a.single(b::contains).toPriority() }
    }

    part2 {
        lines.map(String::toSet).chunked(3).sumOf { (a, b, c) ->
            a.filter(b::contains).single(c::contains).toPriority()
        }
    }
})

object Day03Strings : Day03DSL(body = {
    fun String.splitAt(idx: Int) = substring(0, idx) to substring(idx)

    part1 {
        lines.sumOf {
            val (a, b) = it.splitAt(it.length / 2)
            a.first(b::contains).toPriority()
        }
    }

    part2 {
        lines.chunked(3).sumOf { (a, b, c) ->
            a.first { it in b && it in c }.toPriority()
        }
    }
})

object Day03Bitset : Day03DSL(body = {
    fun String.toBitset() = fold(0L) { v, c -> v or (1L shl c.toPriority()) }

    part1 {
        lines.sumOf {
            val a = it.substring(0, it.length / 2).toBitset()
            val b = it.substring(it.length / 2).toBitset()
            (a and b).countTrailingZeroBits()
        }
    }

    part2 {
        lines.map(String::toBitset).chunked(3)
            .sumOf { (a, b, c) -> (a and b and c).countTrailingZeroBits() }
    }
})
