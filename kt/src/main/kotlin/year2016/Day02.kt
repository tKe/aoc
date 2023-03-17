package year2016

import aok.InputProvider
import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

@AoKSolution
object Day02 : PuzDSL({

    infix fun String.tr(to: String) = { c: Char -> to.getOrNull(indexOf(c)) ?: c }

    fun PuzzleInput.decode(translations: Map<Char, (Char) -> Char>) = lines.runningFold('5') { digit, line ->
        line.fold(digit) { finger, direction ->
            translations.getValue(direction)(finger)
        }
    }.drop(1)

    fun PuzzleInput.decode(up: String, down: String, left: String, right: String) = decode(
        mapOf(
            'D' to (up tr down),
            'U' to (down tr up),
            'L' to (right tr left),
            'R' to (left tr right)
        )
    )

    part1 {
        decode("123456", "456789", "147258", "258369")
    }

    part2 {
        decode("1234678B", "3678ABCD", "837B26A5", "948C37B6")
    }
})


fun main() = solveDay(
    2,
//    input = InputProvider.raw("""
//        ULL
//        RRDDD
//        LURDL
//        UUUUD
//    """.trimIndent())
)
