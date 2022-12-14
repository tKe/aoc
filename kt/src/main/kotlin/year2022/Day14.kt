package year2022

import InputScopeProvider
import aok.PuzzleInput
import aoksp.AoKSolution
import queryPuzzles
import solveAll
import java.util.BitSet

fun main(): Unit = with(InputScopeProvider) {
    queryPuzzles { year == 2022 && day == 14 }.solveAll(
        warmupIterations = 50, runIterations = 3
    )
}

@AoKSolution
object Day14 {
    context(PuzzleInput)
    fun part1() = simulateSand(parseRocks())

    context(PuzzleInput)
    fun part2() = simulateSand(parseRocks().let {
        object : BitSet2D {
            override val depth: Int = it.depth + 2
            override fun get(x: Int, y: Int) = y == depth || it[x, y]
        }
    })

    private fun simulateSand(rocks: BitSet2D) =
        with(ArrayBitSet2D()) { countWhile { addSand(rocks) } }

    private fun MutableBitSet2D.addSand(rocks: BitSet2D): Boolean {
        var (x, y) = 500 to 0
        if (this[x, y]) return false
        fun isClear(x: Int, y: Int) = !(this[x, y] || rocks[x, y])
        while (y <= rocks.depth) when {
            isClear(x, ++y) -> continue
            isClear(x - 1, y) -> x--
            isClear(x + 1, y) -> x++
            else -> {
                mark(x, y - 1)
                return true
            }
        }
        return false
    }

    context(PuzzleInput)
    private fun parseRocks() = ArrayBitSet2D().apply {
        fun range(a: Int, b: Int) = if (a < b) a..b else b..a
        lines.forEach { line ->
            line.splitToSequence(" -> ", ",").map(String::toInt)
                .windowed(4, 2).forEach { (x1, y1, x2, y2) ->
                    if (x1 == x2) range(y1, y2).forEach { y -> mark(x1, y) }
                    else if (y1 == y2) range(x1, x2).forEach { x -> mark(x, y1) }
                }
        }
    }

    private interface BitSet2D {
        val depth: Int
        operator fun get(x: Int, y: Int): Boolean
    }

    private interface MutableBitSet2D : BitSet2D {
        fun mark(x: Int, y: Int)
    }

    private fun countWhile(block: () -> Boolean): Int {
        var count = 0
        while (block()) count++
        return count
    }

    private class ArrayBitSet2D : MutableBitSet2D {
        private var data = Array(0) { BitSet() }
        override var depth = 0
            private set

        override operator fun get(x: Int, y: Int) = y < data.size && data[y].get(x)
        override fun mark(x: Int, y: Int) {
            if (y >= data.size) data = Array(y + 1) { if (it < data.size) data[it] else BitSet(x) }
            depth = maxOf(depth, y)
            data[y].set(x)
        }
    }
}
