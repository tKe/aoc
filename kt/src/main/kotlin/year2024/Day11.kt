package year2024

import aok.PuzzleInput
import aok.checkAll
import aok.solveAll
import aok.warmup
import aoksp.AoKSolution
import utils.splitLongs

@AoKSolution
object Day11 {
    private inline fun <R> Long.evolve(f: (Long, Long) -> R): R = when (this) {
        0L -> f(1L, -1L)
        in 10L..99L -> f(this / 10, this % 10)
        in 1000L..9999L -> f(this / 100, this % 100)
        in 100000L..999999L -> f(this / 1000, this % 1000)
        in 10000000L..99999999L -> f(this / 10000, this % 10000)
        in 1000000000L..9999999999L -> f(this / 100000, this % 100000)
        in 100000000000L..999999999999L -> f(this / 1000000, this % 1000000)
        in 10000000000000L..99999999999999L -> f(this / 10000000, this % 10000000)
        in 1000000000000000L..9999999999999999L -> f(this / 100000000, this % 100000000)
        in 100000000000000000L..999999999999999999L -> f(this / 1000000000, this % 1000000000)
        else -> f(this * 2024, -1L)
    }

    private fun MutableMap<Pair<Long, Int>, Long>.count(stone: Long, steps: Int): Long = getOrPut(stone to steps) {
        when {
            steps == 0 -> 1
            else -> stone.evolve { l, r ->
                count(l, steps - 1) + when (r) {
                    -1L -> count(r, steps - 1)
                    else -> 0
                }
            }
        }
    }

    private fun List<Long>.countStones(steps: Int, cache: MutableMap<Pair<Long, Int>, Long> = mutableMapOf()) =
        sumOf { cache.count(it, steps) }

    context(PuzzleInput) fun part1() = input.splitLongs().countStones(25)
    context(PuzzleInput) fun part2() = input.splitLongs().countStones(75)
}

@AoKSolution
object Day11CountUnique {
    private inline fun <R> Long.evolve(f: (Long, Long) -> R): R = when (this) {
        0L -> f(1L, -1L)
        in 10L..99L -> f(this / 10, this % 10)
        in 1000L..9999L -> f(this / 100, this % 100)
        in 100000L..999999L -> f(this / 1000, this % 1000)
        in 10000000L..99999999L -> f(this / 10000, this % 10000)
        in 1000000000L..9999999999L -> f(this / 100000, this % 100000)
        in 100000000000L..999999999999L -> f(this / 1000000, this % 1000000)
        in 10000000000000L..99999999999999L -> f(this / 10000000, this % 10000000)
        in 1000000000000000L..9999999999999999L -> f(this / 100000000, this % 100000000)
        in 100000000000000000L..999999999999999999L -> f(this / 1000000000, this % 1000000000)
        else -> f(this * 2024, -1L)
    }

    private fun Map<Long, Long>.evolve(): Map<Long, Long> = buildMap(size * 2) {
        for ((stone, count) in this@evolve) {
            fun add(next: Long) = put(next, getOrDefault(next, 0L) + count)
            stone.evolve { a, b ->
                add(a)
                if (b != -1L) add(b)
            }
        }
    }

    private fun List<Long>.countStones(steps: Int): Long {
        var stones = groupingBy { it }.eachCount().mapValues { it.value.toLong() }
        repeat(steps) { stones = stones.evolve() }
        return stones.values.sum()
    }

    context(PuzzleInput) fun part1() = input.splitLongs().countStones(25)
    context(PuzzleInput) fun part2() = input.splitLongs().countStones(75)
}

fun main() {
    queryDay(11)
        .checkAll(55312L) { "125 17" }
        .checkAll(207683L, 244782991106220L)
//        .warmup(1.4, window = 20)
        .warmup()
        .solveAll(20)
}