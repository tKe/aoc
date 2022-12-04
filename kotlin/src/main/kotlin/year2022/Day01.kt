package year2022

import InputScope
import Puz
import PuzzleDefinition
import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf

sealed interface Day01Puz : Puz<Int, Int>
sealed class Day01Base(variant: String? = null) : Day01Puz, Puz22Base<Int, Int>(1, variant)
sealed class Day01DSL(variant: String? = null, def: PuzzleDefinition<Int, Int>): Day01Puz, Puz22DSL<Int, Int>(1, variant, def)

object Day01 : Day01Base() {
    private fun InputScope.elves() = input.splitToSequence("(?:\r?\n){2}".toRegex())
        .map { it.lines().mapNotNull(String::toIntOrNull).sum() }

    context(InputScope) override fun part1() = elves().max()
    context(InputScope) override fun part2() = elves().sortedDescending().take(3).sum()
}

object Day01Nel : Day01DSL("NonEmptyList", {
    fun InputScope.nelElves() = lines.map(String::toIntOrNull).fold(nonEmptyListOf(0)) { acc, i ->
        if (i == null) NonEmptyList(0, acc)
        else NonEmptyList(acc.head + i, acc.tail)
    }
    part1 { nelElves().max() }
    part2 { nelElves().sortedDescending().take(3).sum() }
})

object Day01SummedRuns : Day01DSL("SummedRuns", {
    fun InputScope.summedRuns() = buildList {
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

object Day01TopN : Day01DSL("Top N Elves", {
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

fun main() = Puz.solveAll<Day01Puz>()
