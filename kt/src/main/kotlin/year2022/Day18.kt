@file:Suppress("DuplicatedCode")

package year2022

import InputScopeProvider
import aok.PuzzleInput
import aoksp.AoKSolution
import queryPuzzles
import solveAll

fun main(): Unit = with(InputScopeProvider) {
    queryPuzzles { year == 2022 && day == 18 }.solveAll(
        warmupIterations = 1000, runIterations = 5
    )
}

private typealias Grid = Array<Array<BooleanArray>>

@AoKSolution
object Day18 {

    context(PuzzleInput)
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

    context(PuzzleInput)
    fun part1() = parse().surfaceArea()

    context(PuzzleInput)
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

    private fun Grid.filled(): Array<Array<BooleanArray>> {
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
