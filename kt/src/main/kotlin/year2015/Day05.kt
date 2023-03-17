package year2015

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day05 : PuzDSL({

    part1 {
        val naughties = listOf("ab", "cd", "pq", "xy")
        lines.count {
            (it.count("aeiou"::contains) >= 3)
                    && it.zipWithNext(Char::equals).any()
                    && naughties.none(it::contains)
        }
    }

    part2 {
        val patterns = listOf("(..).*\\1", "(.).\\1").map(String::toRegex)
        lines.count { patterns.all(it::contains) }
    }

})

fun main() = solveDay(5)
