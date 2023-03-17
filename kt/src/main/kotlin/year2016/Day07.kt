package year2016

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day07 : PuzDSL({
    fun String.splitIP() =
        split('[', ']').withIndex()
            .groupBy({ it.index % 2 == 0 }, { it.value })
            .let { it[true].orEmpty() to it[false].orEmpty() }

    val abbaPattern = """([a-z])((?!\1)[a-z])\2\1""".toRegex()
    fun String.supportsTLS() = splitIP().let { (supers, hypers) ->
        supers.any(abbaPattern::containsMatchIn) && hypers.none(abbaPattern::containsMatchIn)
    }

    part1 {
        lines.count(String::supportsTLS)
    }

    val abaPattern = """([a-z])((?!\1)[a-z])\1""".toRegex()
    fun Regex.findAllOverlapping(s: String) =
        generateSequence({ find(s) }) { find(s, it.range.first + 1) }
    fun String.supportsSSL() = splitIP().let { (supers, hypers) ->
        supers.any { s ->
            abaPattern.findAllOverlapping(s).any { aba ->
                val (a, b) = aba.destructured
                hypers.any { "$b$a$b" in it }
            }
        }
    }

    part2 {
        lines.count(String::supportsSSL)
    }
})

fun main() = solveDay(
    7,
//    input = InputProvider.raw(
//        """
//            aba[bab]xyz
//            xyx[xyx]xyx
//            aaa[kek]eke
//            zazbz[bzb]cdb
//        """.trimIndent()
//    )
)
