package year2022

import PuzzleDefinition

fun main() = Puz.solveAll<Day06DSL>(iterations = 10_000)

sealed class Day06DSL(body: PuzzleDefinition<Int, Int>, variant: String? = null) :
    Puz22DSL<Int, Int>(6, variant, body)

object Day06 : Day06DSL({
    fun String.detectUniqueIndex(length: Int) =
        windowedSequence(length, partialWindows = false)
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
        val counts = IntArray(26)
        operator fun IntArray.plusAssign(c: Char) { this[c - 'a'] += 1 }
        operator fun IntArray.minusAssign(c: Char) { this[c - 'a'] -= 1 }
        for ((index, value) in this.withIndex()) {
            counts += value
            if (index >= length && counts.all { it == 0 || it == 1 }) {
                return index + 1
            }
            if (index + 1 >= length) {
                counts -= this[index + 1 - length]
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
