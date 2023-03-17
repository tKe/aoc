package year2022

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(6, runs = 10_000)

@AoKSolution
object Day06 : PuzDSL({
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

@AoKSolution
object Day06IntArray : PuzDSL({
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
