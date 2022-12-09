@file:OptIn(ExperimentalStdlibApi::class)

package year2022

import InputScope
import PuzzleDefinition
import solveAll

fun main() = solveAll<Day08String>()

sealed class Day08DSL(body: PuzzleDefinition<Int, Int>, variant: String? = null) :
    Puz22DSL<Int, Int>(8, variant, body)

object Day08String : Day08DSL({
    part1 {
        val stride = input.indexOf('\n') + 1
        IntArray(input.length).also { marks ->
            val rows = 0..<input.length / stride
            val cols = 0..<(stride - 1)
            for (y in rows) cols.markVisible(input, marks) { it + y * stride }
            for (x in cols) rows.markVisible(input, marks) { x + it * stride }
        }.sum()
    }

    part2 {
        val stride = input.indexOf('\n') + 1
        input.withIndex().maxOf { (idx, height) ->
            val colStart = idx - (idx % stride)
            input.countUntil(height, idx + 1, end = colStart + stride - 1) * // right
                    input.countBackUntil(height, idx - 1, end = colStart) * // left
                    input.countBackUntil(height, idx - stride, stride) * // up
                    input.countUntil(height, idx + stride, stride) // down
        }
    }
})

private inline fun IntRange.markVisible(
    input: String, marks: IntArray,
    index: (Int) -> Int
) {
    var maxHeight = Char.MIN_VALUE

    var lastMarked = -1
    for (it in this) {
        val idx = index(it)
        val height = input[idx]
        if (height > maxHeight) {
            marks[idx] = 1
            maxHeight = height
            lastMarked = it
        }
        if (maxHeight == '9') break // won't be any taller trees
    }

    maxHeight = Char.MIN_VALUE
    for (it in last downTo lastMarked + 1) {
        val idx = index(it)
        val height = input[idx]
        if (height > maxHeight) {
            marks[idx] = 1
            maxHeight = height
        }
    }
}

private fun String.countUntil(
    height: Char, start: Int = 0,
    step: Int = 1, end: Int = length
): Int {
    var count = 0
    var it = start
    while (it < end) {
        count++
        if (this[it] >= height) return count
        it += step
    }
    return count
}

private fun String.countBackUntil(
    height: Char, start: Int = lastIndex,
    step: Int = 1, end: Int = 0
): Int {
    var count = 0
    var it = start
    while (it >= end) {
        count++
        if (this[it] >= height) return count
        it -= step
    }
    return count
}

object Day08 : Day08DSL({
    class IntGrid(val data: IntArray, val stride: Int) : Iterable<Triple<Int, Int, Int>> {
        init {
            require(data.size % stride == 0) {
                "invalid stride $stride for array of size ${data.size}"
            }
        }

        val rows = 0..<data.size / stride
        val cols = 0..<stride
        operator fun get(x: Int, y: Int) = data[x + y * stride]
        operator fun set(x: Int, y: Int, v: Int) {
            data[x + y * stride] = v
        }

        override fun iterator(): Iterator<Triple<Int, Int, Int>> = iterator {
            for ((idx, v) in data.withIndex()) yield(Triple(idx % stride, idx / stride, v))
        }
    }

    fun InputScope.readGrid() = IntGrid(
        data = input.mapNotNull(Char::digitToIntOrNull).toIntArray(),
        stride = input.indexOf('\n')
    )

    fun IntGrid.viewsFrom(x: Int, y: Int) = sequenceOf(
        sequence { for (it in (x - 1) downTo 0) yield(get(it, y)) },
        sequence { for (it in (x + 1)..cols.last) yield(get(it, y)) },
        sequence { for (it in (y - 1) downTo 0) yield(get(x, it)) },
        sequence { for (it in (y + 1)..rows.last) yield(get(x, it)) },
    )

    fun IntGrid.isEdge(x: Int, y: Int) =
        x == 0 || y == 0 || x == cols.last || y == rows.last

    part1 {
        fun IntGrid.isVisible(x: Int, y: Int) =
            isEdge(x, y) || get(x, y).let { height ->
                viewsFrom(x, y).any { view -> view.all { it < height } }
            }

        with(readGrid()) {
            count { (x, y) -> isVisible(x, y) }
        }
    }

    part2 {
        fun IntGrid.scenicScore(x: Int, y: Int) =
            if (isEdge(x, y)) 0
            else get(x, y).let { height ->
                viewsFrom(x, y).fold(1) { acc, view ->
                    var score = 0
                    for (tree in view) {
                        score += acc
                        if (tree >= height) break
                    }
                    score
                }
            }

        with(readGrid()) { maxOf { (x, y) -> scenicScore(x, y) } }
    }
})