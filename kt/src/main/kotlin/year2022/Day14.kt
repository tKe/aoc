package year2022

import aok.InputProvider
import aok.PuzzleInput
import aoksp.AoKSolution
import aok.solveAll
import aok.warmup
import java.util.BitSet
import kotlin.collections.ArrayDeque

fun main(): Unit = with(InputProvider) {
    queryDay(11).warmup(
        iterations = 300
    ).solveAll(
        runIterations = 3
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

@AoKSolution
object Day14Stack {
    context(PuzzleInput)
    fun part1() = BitSetCave().apply { populateRocks() }.simulateSand()

    context(PuzzleInput)
    fun part2() = BitSetCave().apply { populateRocks() }.let {
        val floor = it.depth + 2
        object : Cave by it {
            override val depth = floor + 1
            override fun get(x: Int, y: Int) = y == floor || it[x, y]
        }.run { simulateSand() }
    }

    private fun Cave.simulateSand(): Int {
        var count = 0
        val next = ArrayDeque(listOf(500 to 0))
        while (next.isNotEmpty()) {
            var (x, y) = next.removeFirst()
            while (!get(x, y)) when {
                y > depth -> return count
                !get(x, y + 1) -> next.addFirst(Pair(x, y++))
                !get(x - 1, y + 1) -> next.addFirst(Pair(x--, y++))
                !get(x + 1, y + 1) -> next.addFirst(Pair(x++, y++))
                else -> mark(x, y)
            }
            count++
        }
        return count
    }

    context(PuzzleInput, Cave)
    private fun populateRocks() {
        fun range(a: Int, b: Int) = if (a < b) a..b else b..a
        lines.forEach { line ->
            line.splitToSequence(" -> ", ",").map(String::toInt)
                .windowed(4, 2).forEach { (x1, y1, x2, y2) ->
                    if (x1 == x2) range(y1, y2).forEach { y -> mark(x1, y) }
                    else if (y1 == y2) range(x1, x2).forEach { x -> mark(x, y1) }
                }
        }
    }

    private interface Cave {
        val depth: Int
        operator fun get(x: Int, y: Int): Boolean
        fun mark(x: Int, y: Int)
    }

    private class BitSetCave : Cave {
        private fun idx(x: Int, y: Int) = x + ((y + x) * (y + x + 1) shr 1)
        private var data = BitSet()
        override var depth = 0
            private set

        override operator fun get(x: Int, y: Int) = data[idx(x, y)]
        override fun mark(x: Int, y: Int) {
            data.set(idx(x, y))
            if (y > depth) depth = y
        }
    }
}
