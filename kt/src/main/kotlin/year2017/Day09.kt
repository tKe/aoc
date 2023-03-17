package year2017

import aok.InputProvider
import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

@AoKSolution
object Day09 : PuzDSL({

    fun String.assessStream(): Pair<Int, Int> {
        val stack = ArrayDeque<Char>()
        var escapeNext = false
        var garbage = false
        fun escapeOr(block: () -> Unit) = if (escapeNext) escapeNext = false else block()
        var count = 0
        var score = 0
        forEach { c ->
            escapeOr {
                when (c) {
                    '!' -> escapeNext = true
                    '{' -> if (garbage) count++ else stack += c
                    '}' -> if (garbage) count++ else {
                        score += stack.size
                        check(stack.removeFirst() == '{')
                    }
                    '<' -> if (garbage) count++ else garbage = true
                    '>' -> garbage = false
                    else -> if (garbage) count++
                }
            }
        }
        return score to count
    }

    part1 { input.assessStream().first }
    part2 { input.assessStream().second }
})


fun main(): Unit = solveDay(9,
//    input = InputProvider.raw("<{o\"i!a,<{i<a>")
)
