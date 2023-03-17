package year2015

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day08 : PuzDSL({
    val pattern = """(\\x[a-fA-F0-9]{2}|\\[\\"])""".toRegex()
    fun List<String>.calculate() = sumOf(String::length) - sumOf {
        it.trim('"').replace(pattern, "-").length
    }

    part1 { lines.calculate() }

    part2 {
        lines.map {
            "\"${it.replace("\\", "\\\\").replace("\"", "\\\"")}\""
        }.calculate()
    }
})

fun main() = solveDay(
    8,
//    input = InputProvider.raw("""
//        ""
//        "abc"
//        "aaa\"aaa"
//        "\x27"
//    """.trimIndent())
)
