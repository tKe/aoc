package year2018

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day02 : PuzDSL({
    fun String.letterCounts() = groupingBy { it }.eachCount().values.toSet()

    part1 {
        val counts = lines.map(String::letterCounts)
        counts.count { 2 in it } * counts.count { 3 in it }
    }

    part2 {
        lines.firstNotNullOf {a ->
            lines.firstNotNullOfOrNull { b ->
                val matches = a.zip(b) { c, d -> c != d }
                if (matches.count { it } == 1) {
                    val idx = matches.indexOf(true)
                    a.substring(0, idx) + a.substring(idx + 1)
                } else null
            }
        }
    }
})

fun main(): Unit = solveDay(2)
