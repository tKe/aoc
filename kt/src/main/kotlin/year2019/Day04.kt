package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import kotlin.math.absoluteValue

fun main(): Unit = solveDay(
    4,
)

@AoKSolution
object Day04 : PuzDSL({
    val parse = parser { input.split("-").let { (a, b) -> a.toInt()..b.toInt() } }
    part1(parse) { range ->
        fun Int.isPassword(): Boolean {
            var hasDouble = false
            for ((a, b) in toString().zipWithNext()) {
                if (b < a) return false
                if (a == b) hasDouble = true
            }
            return hasDouble
        }
        range.count(Int::isPassword)
    }
    part2(parse) { range ->
        fun Int.isPassword(): Boolean {
            for ((a, b) in toString().zipWithNext()) {
                if (b < a) return false
            }
            return Regex("([0-9])\\1+").findAll(toString()).any { it.value.length == 2 }
        }
        range.count(Int::isPassword)
    }
})
