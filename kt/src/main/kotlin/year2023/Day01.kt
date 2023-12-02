package year2023

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(
    1, runs = 100, warmup = aok.Warmup.iterations(1000)
)

@AoKSolution
object Day01 : PuzDSL({
    part1 {
        lines.sumOf { 10 * it.firstNotNullOf(Char::digitToIntOrNull) + it.lastOrNull(Char::isDigit)!!.digitToInt() }
    }
    part2 {
        val names = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
        val backNames = names.map(String::reversed)
        fun String.numFrom(names: List<String>): Int {
            val nind = indexOfAny(names)
            val cind = indexOfFirst { it.isDigit() }
            return if ((nind == -1 && cind != -1) || cind < nind) get(cind).digitToInt()
            else names.indexOfFirst { it.startsWith(substring(nind, nind + 3)) } + 1
        }
        lines.sumOf { 10 * it.numFrom(names) + it.reversed().numFrom(backNames) }
    }
})

@AoKSolution
object Day01FindAnyOf : PuzDSL({
    part1 {
        lines.sumOf {
            val first = it.firstNotNullOf(Char::digitToIntOrNull)
            val last = it.lastOrNull(Char::isDigit)!!.digitToInt()
            10 * first + last
        }
    }
    part2 {
        val digits = listOf(
            "one", "two", "three", "four", "five",
            "six", "seven", "eight", "nine",
        ) + (1..9).map(Int::toString)

        fun Pair<Int, String>?.value() =
            if (this == null) 0
            else second.singleOrNull()?.digitToInt()
                ?: digits.indexOf(second).inc()

        lines.sumOf {
            val first = it.findAnyOf(digits).value()
            val last = it.findLastAnyOf(digits).value()
            10 * first + last
        }
    }
})