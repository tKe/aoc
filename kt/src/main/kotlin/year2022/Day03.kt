package year2022

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(3)

fun Char.toPriority() = this - if (this >= 'a') 'a' - 1 else 'A' - 27

@AoKSolution
object Day03Sets : PuzDSL({
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

@AoKSolution
object Day03Strings : PuzDSL({
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

@AoKSolution
object Day03Bitset : PuzDSL({
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
