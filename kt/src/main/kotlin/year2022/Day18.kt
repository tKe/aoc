@file:Suppress("DuplicatedCode")

package year2022

import aok.InputProvider
import aok.PuzzleInput
import aok.lines
import aoksp.AoKSolution
import aok.solveAll
import aok.warmup
import java.util.BitSet
import kotlin.collections.ArrayDeque
import kotlin.collections.Iterable
import kotlin.collections.asSequence
import kotlin.collections.buildList
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.collections.count
import kotlin.collections.filter
import kotlin.collections.flatMapIndexed
import kotlin.collections.forEach
import kotlin.collections.getOrNull
import kotlin.collections.indices
import kotlin.collections.isNotEmpty
import kotlin.collections.listOf
import kotlin.collections.map
import kotlin.collections.maxOf
import kotlin.collections.mutableSetOf
import kotlin.collections.plusAssign

fun main(): Unit = with(InputProvider) {
    queryDay(18).warmup(
        iterations = 2000
    ).solveAll(
        runIterations = 5
    )
}

@AoKSolution
object Day18Points {

    class BitSet3(val data: BitSet, val yStride: Int, val zStride: Int) {
        inline fun sumOf(block: (Int) -> Int): Int {
            var sum = 0
            val bitmap = data.toLongArray()
            for (i in bitmap.indices) {
                var word: Long = bitmap[i]
                while (word != 0L) {
                    sum += block(Long.SIZE_BITS * i + word.countTrailingZeroBits())
                    word = word xor word.takeLowestOneBit()
                }
            }
            return sum
        }
    }

    context(_: PuzzleInput)
    private fun parse() = lines
        .map { line ->
            line.split(',').map {
                it.toInt() + 1 // ensure space for air
            }
        }
        .let { points ->
            val yStride = points.maxOf { it[0] + 1 }
            val zStride = points.maxOf { it[1] + 1 } * yStride
            BitSet3(BitSet().apply {
                points.forEach { set(it[2] * zStride + it[1] * yStride + it[0]) }
            }, yStride, zStride)
        }

    context(_: PuzzleInput)
    fun part1() = parse().surfaceArea()

    context(_: PuzzleInput)
    fun part2(): Int = with(parse()) {
        val filled = BitSet().apply { set(1, data.length()) }
        val queue = ArrayDeque(listOf(0))
        fun checkAndClear(n: Int) {
            if (filled[n] && !data[n]) {
                queue += n
                filled.clear(n)
            }
        }
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            if (node >= 1) checkAndClear(node - 1)
            if (node >= yStride) checkAndClear(node - yStride)
            if (node >= zStride) checkAndClear(node - zStride)
            checkAndClear(node + 1)
            checkAndClear(node + yStride)
            checkAndClear(node + zStride)
        }
        surfaceArea { !filled[it] }
    }

    private inline fun BitSet3.surfaceArea(external: (Int) -> Boolean = { !data[it] }) = sumOf {
        (if (external(it + 1)) 1 else 0) +
                (if (external(it - 1)) 1 else 0) +
                (if (external(it + yStride)) 1 else 0) +
                (if (external(it - yStride)) 1 else 0) +
                (if (external(it + zStride)) 1 else 0) +
                (if (external(it - zStride)) 1 else 0)
    }
}

private typealias Grid = Array<Array<BooleanArray>>

@AoKSolution
object Day18 {

    context(_: PuzzleInput)
    private fun parse(): Grid = lines.map {
        it.split(',').map(String::toInt)
    }.let { points ->
        val maxX = points.maxOf { it[0] }
        val maxY = points.maxOf { it[1] }
        val maxZ = points.maxOf { it[2] }
        Array(maxZ + 1) {
            Array(maxY + 1) { BooleanArray(maxX + 1) }
        }.also { grid ->
            points.forEach { (x, y, z) -> grid[x, y, z] = true }
        }
    }

    context(_: PuzzleInput)
    fun part1() = parse().surfaceArea()

    context(_: PuzzleInput)
    fun part2() = parse().filled().surfaceArea()

    private val Grid.dimensions
        get() = Triple(
            getOrNull(0)?.getOrNull(0)?.indices ?: IntRange.EMPTY,
            getOrNull(0)?.indices ?: IntRange.EMPTY,
            indices
        )

    private operator fun Grid.get(x: Int, y: Int, z: Int) =
        getOrNull(z)?.getOrNull(y)?.getOrNull(x) ?: false

    private operator fun Grid.set(x: Int, y: Int, z: Int, value: Boolean) =
        run { this[z][y][x] = value }

    private fun neighbours(x: Int, y: Int, z: Int) = buildList {
        add(Triple(x + 1, y, z))
        add(Triple(x - 1, y, z))
        add(Triple(x, y + 1, z))
        add(Triple(x, y - 1, z))
        add(Triple(x, y, z + 1))
        add(Triple(x, y, z - 1))
    }

    private val Grid.points
        get() = asSequence().flatMapIndexed { z, ys ->
            ys.flatMapIndexed { y, xs ->
                xs.indices.map { x -> Triple(x, y, z) }
            }
        }

    private fun Grid.surfaceArea() = points.filter { (x, y, z) -> this[x, y, z] }
        .sumOf { (x, y, z) -> neighbours(x, y, z).count { (nx, ny, nz) -> !this[nx, ny, nz] } }

    private fun Grid.filled(): Grid {
        val (xs, ys, zs) = dimensions
        val exs = -1..xs.last + 1
        val eys = -1..ys.last + 1
        val ezs = -1..zs.last + 1
        val filled = Array(zs.count()) { Array(ys.count()) { BooleanArray(xs.count()) { true } } }
        breadthFirstSearch(Triple(-1, -1, -1),
            { (x, y, z) ->
                if (x in xs && y in ys && z in zs) filled[x, y, z] = false
                false
            }) { (x, y, z) ->
            neighbours(x, y, z).filter { (nx, ny, nz) ->
                nx in exs && ny in eys && nz in ezs && !this[nx, ny, nz]
            }
        }
        return filled
    }

    private inline fun <T> breadthFirstSearch(
        start: T,
        isEnd: (T) -> Boolean = { false },
        crossinline moves: (T) -> Iterable<T>,
    ): Int {
        val queue = ArrayDeque(listOf(start to 0))
        val visited = mutableSetOf<T>()
        while (queue.isNotEmpty()) {
            val (node, dist) = queue.removeFirst()
            for (neighbour in moves(node)) {
                if (isEnd(neighbour)) return dist + 1
                if (visited.add(neighbour)) queue += neighbour to dist + 1
            }
        }
        return -1
    }
}
