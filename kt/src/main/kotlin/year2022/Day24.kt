package year2022

import aok.PuzzleInput
import aok.Warmup
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds

fun main(): Unit = solveDay(
    day = 24,
    warmup = Warmup.eachFor(10.seconds),
)

@AoKSolution
object Day24 {

    context(PuzzleInput)
    fun part1() = solve { entrance travelTo exit }

    context(PuzzleInput)
    fun part2() = solve { entrance travelTo exit travelTo entrance travelTo exit }

    context(PuzzleInput)
    private fun solve(journey: Valley.() -> Pos) = parseValley().run(journey).time

    context(Valley)
    private infix fun Pos.travelTo(end: Pos) = breadthFirstSearch(this, { it at end }) { pos ->
        pos.moves { it at this || it at end || (it !in blizzards) }
    }

    context(PuzzleInput)
    private fun parseValley() = Valley(
        Blizzards.parse(lines),
        Pos(lines.first().indexOf('.') - 1, -1, 0),
        Pos(lines.last().indexOf('.') - 1, lines.lastIndex - 1, -1)
    )

    internal class Blizzards private constructor(val width: Int, val height: Int) {
        private val up: BooleanArray = BooleanArray(height * width)
        private val down: BooleanArray = BooleanArray(height * width)
        private val left: BooleanArray = BooleanArray(height * width)
        private val right: BooleanArray = BooleanArray(height * width)

        private operator fun BooleanArray.get(x: Int, y: Int) = get(y * width + x)
        private operator fun BooleanArray.set(x: Int, y: Int, value: Boolean) = set(y * width + x, value)

        operator fun get(x: Int, y: Int, t: Int) =
            x !in 0..<width || y !in 0..<height
                    || up[x, (y + t).mod(height)]
                    || down[x, (y - t).mod(height)]
                    || left[(x + t).mod(width), y]
                    || right[(x - t).mod(width), y]

        companion object {
            fun parse(lines: List<String>) =
                Blizzards(width = lines.first().length - 2, height = lines.size - 2).apply {
                    lines.drop(1).take(height).forEachIndexed { y, line ->
                        line.drop(1).take(width).forEachIndexed { x, c ->
                            when (c) {
                                '^' -> up[x, y] = true
                                'v' -> down[x, y] = true
                                '<' -> left[x, y] = true
                                '>' -> right[x, y] = true
                            }
                        }
                    }
                }
        }
    }

    private class Valley(val blizzards: Blizzards, val entrance: Pos, val exit: Pos)

    private data class Pos(val x: Int, val y: Int, val time: Int)

    private fun Pos.move(x: Int = 0, y: Int = 0) =
        copy(time = time + 1, x = this.x + x, y = this.y + y)

    private fun Pos.moves(predicate: (Pos) -> Boolean) =
        listOf(move(x = 1), move(x = -1), move(y = 1), move(y = -1), move()).filter(predicate)

    private infix fun Pos.at(other: Pos) =
        this.x == other.x && this.y == other.y

    private operator fun Blizzards.contains(pos: Pos) =
        get(pos.x, pos.y, pos.time)

    private inline fun <T> breadthFirstSearch(start: T, isEnd: (T) -> Boolean, moves: (T) -> Iterable<T>): T {
        val queue = ArrayDeque(listOf(start))
        val visited = mutableSetOf<T>()
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            for (neighbour in moves(node)) {
                if (isEnd(neighbour)) return neighbour
                if (visited.add(neighbour)) queue += neighbour
            }
        }
        error("No route found")
    }
}
