package year2015

import aok.PuzDSL
import aoksp.AoKSolution
import java.util.*

@AoKSolution
object Day20 : PuzDSL({
    part1 {
        val limit = input.trim().toInt()
        IntArray(limit / 10) { 0 }.also { houses ->
            (1..houses.lastIndex).forEach { elf ->
                (elf..houses.lastIndex step elf).forEach { house ->
                    houses[house] += elf * 10
                }
            }
        }.indexOfFirst { it >= limit }
    }

    part2 {
        val limit = input.trim().toInt()
        IntArray(limit / 10) { 0 }.also { houses ->
            (1..houses.lastIndex).forEach { elf ->
                (elf..houses.lastIndex step elf).take(50).forEach { house ->
                    houses[house] += elf * 11
                }
            }
        }.indexOfFirst { it >= limit }
    }
})

fun main() = solveDay(
    20,
)
