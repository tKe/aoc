package year2022

import PuzzleDefinition
import solveAll

fun main() = solveAll<Day06DSL>(runIterations = 10_000)

sealed class Day06DSL(body: PuzzleDefinition<Int, Int>, variant: String? = null) :
    Puz22DSL<Int, Int>(6, variant, body)

object Day06 : Day06DSL({
    fun String.detectUniqueIndex(length: Int) =
        windowedSequence(length)
            .indexOfFirst { it.all(mutableSetOf<Char>()::add) }
            .let { if (it == -1) -1 else it + length }

    part1 {
        input.detectUniqueIndex(4)
    }
    part2 {
        input.detectUniqueIndex(14)
    }
})

object Day06IntArray : Day06DSL({
    fun String.detectUniqueIndex(length: Int) : Int {
        val counts = ByteArray(26)
        for ((idx, c) in withIndex()) {
            counts[c - 'a']++
            if (idx >= length) {
                counts[this[idx - length] - 'a']--
                if (counts.none { it > 1 }) return idx + 1
            }
        }
        return -1
    }

    part1 {
        input.detectUniqueIndex(4)
    }
    part2 {
        input.detectUniqueIndex(14)
    }
})
