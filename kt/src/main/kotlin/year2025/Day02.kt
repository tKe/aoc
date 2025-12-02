package year2025

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day02 : PuzDSL({
    part1 {
        input.split(",").sumOf {
            val range = it.split("-").let { (start, end) -> start.toLong()..end.toLong() }
            range.filter {
                val s = "$it"
                val half = s.length / 2
                s.length == half * 2 && s.take(half) == s.drop(half)
            }.sum()
        }
    }

    part2 {
        val pattern = """^(\d+)\1+$""".toRegex()
        input.split(",").sumOf {
            val range = it.split("-").let { (start, end) -> start.toLong()..end.toLong() }
            range.filter { "$it".matches(pattern) }.sum()
        }
    }
})

fun main() = solveDay(2)//, input = InputProvider.raw("""1188511880-1188511890"""))
