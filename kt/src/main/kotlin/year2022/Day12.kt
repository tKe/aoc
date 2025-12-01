package year2022

import aok.InputProvider
import aok.PuzzleInput
import aok.input
import aok.lines
import aoksp.AoKSolution
import aok.solveAll
import aok.warmup
import java.util.BitSet

fun main() = with(InputProvider) {
    queryDay(12).warmup(iterations = 3000).solveAll(runIterations = 3)
}

@AoKSolution
object Day12 {
    context(_: PuzzleInput)
    fun part1() = with(CharGrid.of(lines)) {
        val start = findPoints('S').single()
        val goal = findPoints('E').single()
        breadthFirstSearch(start, goal::equals) {
            it.neighbours { a, b -> a canTraverseTo b }
        }
    }

    context(_: PuzzleInput)
    fun part2() = with(CharGrid.of(lines)) {
        val start = findPoints('E').single()
        val goals = findPoints('a').toSet()
        breadthFirstSearch(start, goals::contains) {
            it.neighbours { a, b -> b canTraverseTo a }
        }
    }

    private val Char.elevation
        get() = when (this) {
            'S' -> 0
            'E' -> 25
            else -> this - 'a'
        }

    private infix fun Char.canTraverseTo(other: Char) = other.elevation <= elevation + 1

    private class CharGrid(private val data: Array<String>) {
        init {
            require(data.all { it.length == data[0].length }) { "all rows must be of uniform length " }
        }

        val rows = data.indices
        val cols = data[0].indices

        operator fun get(point: Point) = with(point) { data[y][x] }
        operator fun contains(point: Point) = point.y in rows && point.x in cols

        inline fun Point.neighbours(crossinline traversable: (Char, Char) -> Boolean) =
            get(this).let { neighbours().filter { n -> traversable(it, get(n)) } }

        fun Point.neighbours() = listOf(move(x = -1), move(x = 1), move(y = -1), move(y = 1)).filter(::contains)

        companion object {
            fun of(lines: List<String>) = CharGrid(lines.toTypedArray())
        }
    }

    private fun CharGrid.findPoints(char: Char) = sequence {
        for (y in rows) for (x in cols) Point(x, y).also { if (get(it) == char) yield(it) }
    }

    private data class Point(val x: Int, val y: Int)

    private fun Point.move(x: Int = 0, y: Int = 0) = Point(this.x + x, this.y + y)

    private inline fun breadthFirstSearch(
        start: Point, isEnd: (Point) -> Boolean,
        moves: (Point) -> Iterable<Point>
    ): Int {
        val queue = ArrayDeque(listOf(start to 0))
        val visited = mutableSetOf<Point>()
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

@AoKSolution
object Day12String {
    context(_: PuzzleInput)
    fun part1() = with(input) {
        val stride = input.indexOf('\n') + 1
        val start = indexOf('S')
        val goal = indexOf('E')
        breadthFirstSearch(start, goal::equals) {
            moves(it, stride) { a, b -> a canTraverseTo b }
        }
    }

    context(_: PuzzleInput)
    fun part2() = with(input) {
        val stride = input.indexOf('\n') + 1
        val start = indexOf('E')
        breadthFirstSearch(start, { input[it] == 'a' }) {
            moves(it, stride) { a, b -> b canTraverseTo a }
        }
    }

    private val Char.elevation
        get() = when (this) {
            'S' -> 0
            'E' -> 25
            else -> this - 'a'
        }

    private infix fun Char.canTraverseTo(other: Char) = other.elevation <= elevation + 1

    private inline fun String.moves(idx: Int, stride: Int, crossinline valid: (Char, Char) -> Boolean) =
        get(idx).let { current ->
            listOf(idx - stride, idx + stride, idx - 1, idx + 1)
                .filter { it in indices && get(it).let { c -> c != '\n' && valid(current, c) } }
        }

    private inline fun breadthFirstSearch(start: Int, isEnd: (Int) -> Boolean, moves: (Int) -> Iterable<Int>): Int {
        val queue = ArrayDeque(listOf(start to 0))
        val visited = BitSet()
        while (queue.isNotEmpty()) {
            val (node, dist) = queue.removeFirst()
            for (neighbour in moves(node)) {
                if (isEnd(neighbour)) return dist + 1
                if (!visited[neighbour]) queue += neighbour to dist + 1
                visited.set(neighbour)
            }
        }
        return -1
    }
}
