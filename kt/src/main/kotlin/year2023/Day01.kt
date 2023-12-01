package year2023

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(
    1
)

@AoKSolution
object Day01 : PuzDSL({
    part1 {
        input.lineSequence().sumOf { it.mapNotNull { it.digitToIntOrNull() }.let { 10 * it.first() + it.last() } }
    }
    part2 {
        val names = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
        val backNames = names.map(String::reversed)
        fun String.numFrom(names: List<String>): Int {
            val nind = indexOfAny(names)
            val cind = indexOfFirst { it.isDigit() }
            return if ((nind == -1 && cind != -1) || cind < nind) get(cind).digitToInt()
            else names.indexOfFirst {it.startsWith(substring(nind, nind + 3)) } + 1
        }
        input.lineSequence().sumOf { 10*it.numFrom(names) + it.reversed().numFrom(backNames) }
    }
})