package year2024

import aok.PuzzleInput
import aok.checkAll
import aok.solveAll
import aok.warmupEach
import aoksp.AoKSolution
import utils.Parsers
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day22 {
    context(PuzzleInput) fun part1() = Parsers.Longs().sumOf {
        generateSequence(it) { it.evolve() }.elementAt(2000)
    }

    context(PuzzleInput) fun part2() = Parsers.Longs()
        .flatMap { secret ->
            generateSequence(secret) { it.evolve() }
                .map { it % 10 }.take(2001)
                .windowed(5) { (a, b, c, d, e) ->
                    "${b - a} ${c - b} ${d - c} ${e - d}" to e.toInt()
                }
                .distinctBy { it.first }
        }
        .groupingBy { it.first }
        .fold(0) { acc, element -> acc + element.second }
        .maxOf { it.value }

    fun Long.evolve(): Long {
        var sec = this
        sec = sec shl 6 xor sec and 16777215
        sec = sec shr 5 xor sec and 16777215
        sec = sec shl 11 xor sec and 16777215
        return sec
    }
}

@AoKSolution
object Day22Array {
    context(PuzzleInput) fun part1() = Parsers.Longs().sumOf {
        var s = it
        repeat(2000) { s = s.evolve() }
        s
    }

    context(PuzzleInput) fun part2(): Int {
        val bananas = IntArray(0xFFFFF)
        val seen = IntArray(0xFFFFF) { -1 }
        var max = 0

        Parsers.Longs().forEachIndexed { buyer, secret ->
            var s = secret
            var prevPrice = (s % 10).toInt()
            var delta = 0
            for (i in 0..<2000) {
                s = s.evolve()
                val price = (s % 10).toInt()
                delta = (delta shl 5 and 0xFFFFF) or (9 + price - prevPrice)
                prevPrice = price
                if (i >= 3 && seen[delta] < buyer) {
                    seen[delta] = buyer
                    val newBananas = bananas[delta] + price
                    bananas[delta] = newBananas
                    max = maxOf(newBananas, max)
                }
            }
        }

        return max
    }

    fun Long.evolve(): Long {
        var sec = this
        sec = sec shl 6 xor sec and 16777215
        sec = sec shr 5 xor sec and 16777215
        sec = sec shl 11 xor sec and 16777215
        return sec
    }
}

fun main(): Unit = queryDay(22)
    .checkAll(
        input = """
            1
            10
            100
            2024
        """.trimIndent(),
        part1 = 37327623,
    )
    .checkAll(
        input = """
            1
            2
            3
            2024
        """.trimIndent(),
        part2 = 23,
    )
    .checkAll(16039090236, 1808)
    .warmupEach(10.seconds)
    .solveAll(30)
