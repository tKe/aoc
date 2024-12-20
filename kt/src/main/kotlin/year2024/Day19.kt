package year2024

import aok.PuzzleInput
import aok.checkAll
import aok.solveAll
import aok.warmup
import aoksp.AoKSolution
import year2023.CachedDeepRecursiveFunction
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day19 {
    context(PuzzleInput) fun parse() = input.split("\n\n", limit = 2)
        .let { (towels, patterns) -> towels.split(", ") to patterns.lines() }

    context(PuzzleInput) fun part1(): Int {
        val (towels, patterns) = parse()
        val validate = towels.joinToString("|", prefix = "(", postfix = ")+").toRegex()
        return patterns.count {
            validate.matchEntire(it) != null
        }
    }

    context(PuzzleInput) fun part2(): Long {
        val (towels, patterns) = parse()
        val solutions = CachedDeepRecursiveFunction<Pair<String, Int>, Long> { (pattern, idx) ->
            if (idx == pattern.length) return@CachedDeepRecursiveFunction 1L
            towels.sumOf {
                when {
                    pattern.startsWith(it, startIndex = idx) -> callRecursive(pattern to idx + it.length)
                    else -> 0L
                }
            }
        }
        return patterns.sumOf { solutions(it to 0) }
    }
}

fun main() {
    queryDay(19)
        .checkAll(
            input = """
                r, wr, b, g, bwu, rb, gb, br

                brwrr
                bggr
                gbbr
                rrbgbr
                ubwu
                bwurrg
                brgr
                bbrgwb
            """.trimIndent(),
            part1 = 6,
        )
        .checkAll(part1 = 347, part2 = 919219286602165L)
        .warmup(10.seconds)
        .solveAll()
}
