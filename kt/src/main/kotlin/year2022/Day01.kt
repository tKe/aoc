package year2022

import PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution
import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf

@AoKSolution
object Day01 {
    private fun PuzzleInput.elves() = input.splitToSequence("(?:\r?\n){2}".toRegex())
        .map { it.lines().mapNotNull(String::toIntOrNull).sum() }

    context(PuzzleInput) fun part1() = elves().max()
    context(PuzzleInput) fun part2() = elves().sortedDescending().take(3).sum()
}

@AoKSolution(variant = "NonEmptyList")
object Day01Nel : PuzDSL({
    fun PuzzleInput.nelElves() = lines.map(String::toIntOrNull).fold(nonEmptyListOf(0)) { acc, i ->
        if (i == null) NonEmptyList(0, acc)
        else NonEmptyList(acc.head + i, acc.tail)
    }
    part1 { nelElves().max() }
    part2 { nelElves().sortedDescending().take(3).sum() }
})

@AoKSolution
object Day01SummedRuns : PuzDSL({
    fun PuzzleInput.summedRuns() = buildList {
        var sum = 0
        lines.map(String::toIntOrNull).forEach {
            if (it == null) add(sum)
            sum = it?.plus(sum) ?: 0
        }
        add(sum)
    }
    part1 { summedRuns().max() }
    part2 { summedRuns().sorted().takeLast(3).sum() }
})

@AoKSolution
object Day01TopN : PuzDSL({
    fun List<String>.topElves(n: Int): Int {
        require(n > 0) { "n must be positive, was $n" }
        val top = IntArray(n)
        operator fun IntArray.plusAssign(acc: Int) {
            if (acc > this[0]) {
                var idx = 0
                while (idx < lastIndex && this[idx + 1] < acc) this[idx] = this[++idx]
                this[idx] = acc
            }
        }

        var acc = 0
        for (line in this) {
            when (val value = line.toIntOrNull()) {
                null -> {
                    top += acc
                    acc = 0
                }

                else -> acc += value
            }
        }
        top += acc
        return top.sum()
    }
    part1 { lines.topElves(1) }
    part2 { lines.topElves(3) }
})

fun main() = solveAll(day = 1)
