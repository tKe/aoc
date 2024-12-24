package year2024

import aok.PuzzleInput
import aok.checkAll
import aok.solveAll
import aok.warmup
import aoksp.AoKSolution
import utils.bfs
import utils.bfsRoute
import utils.splitOnce
import year2024.Day18.neighboursIn
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day18 {
    context(PuzzleInput) fun part1(): Int {
        val (dim, count) = if (lines.size < 30) 6 to 12 else 70 to 1024
        val bytes = lineSeq.take(count).map { it.splitOnce(",", String::toInt) }.toSet()
        return bfs(0 to 0, { equals(dim to dim) }) { neighboursIn(dim) { it !in bytes } }
    }

    context(PuzzleInput) fun part2(): String {
        val (dim, count) = if (lines.size < 30) 6 to 12 else 70 to 1024

        val byteStream = lineSeq.map { it.splitOnce(",", String::toInt) }.iterator()
        val bytes = mutableSetOf<Pair<Int, Int>>()
        repeat(count) { if (byteStream.hasNext()) bytes += byteStream.next() }

        fun nextRoute() = route(dim, bytes)

        var route = nextRoute()
        while (byteStream.hasNext()) {
            val next = byteStream.next()
            bytes += next
            if (next in route) {
                try {
                    route = nextRoute()
                } catch (e: Exception) {
                    return "${next.first},${next.second}"
                }
            }
        }
        error("no solution found")
    }

    private fun route(
        dim: Int,
        bytes: MutableSet<Pair<Int, Int>>
    ) = bfsRoute(0 to 0, { equals(dim to dim) }) {
        neighboursIn(dim) { it !in bytes }
    }.toSet()

    inline fun Pair<Int, Int>.neighboursIn(dim: Int, crossinline predicate: (Pair<Int, Int>) -> Boolean) = sequence {
        if (first > 0) copy(first = first - 1).let { if (predicate(it)) yield(it) }
        if (first < dim) copy(first = first + 1).let { if (predicate(it)) yield(it) }
        if (second > 0) copy(second = second - 1).let { if (predicate(it)) yield(it) }
        if (second < dim) copy(second = second + 1).let { if (predicate(it)) yield(it) }
    }
}

@AoKSolution
object Day18BinarySearch {
    context(PuzzleInput) fun part1(): Int {
        val (dim, count) = if (lines.size < 30) 6 to 12 else 70 to 1024
        val bytes = lineSeq.take(count).map { it.splitOnce(",", String::toInt) }.toSet()
        return bfs(0 to 0, { equals(dim to dim) }) { neighboursIn(dim) { it !in bytes } }
    }

    context(PuzzleInput) fun part2(): String {
        val (dim, count) = if (lines.size < 30) 6 to 12 else 70 to 1024

        val byteStream = lines.map { it.splitOnce(",", String::toInt) }
        val bytes = byteStream.take(count).toMutableSet()
        fun check(idx: Int): Boolean {
            if (idx <= bytes.size) return true
            val extraBytes = byteStream.subList(bytes.size, idx).toSet()
            val res = bfs(0 to 0, { equals(dim to dim) }, noRoute = { -1 }) {
                neighboursIn(dim) { it !in bytes && it !in extraBytes }
            }
            if (res != -1) {
                bytes += extraBytes
                return true
            }
            return false
        }

        val candidates = (bytes.size..byteStream.lastIndex).toList()
        val idx = candidates.binarySearch { if (check(it)) -1 else 1 }
        return byteStream[candidates[-idx - 1] - 1].run { "$first,$second" }
    }
}

fun main() {
    queryDay(18)
        .checkAll(
            part1 = 22, part2 = "6,1",
            input = {
                """
                5,4
                4,2
                4,5
                3,0
                2,1
                6,3
                2,4
                1,5
                0,6
                3,3
                2,6
                5,1
                1,2
                5,5
                2,5
                6,5
                1,4
                0,4
                6,4
                1,1
                6,1
                1,0
                0,5
                1,6
                2,0
            """.trimIndent()
            })
        .checkAll(part1 = 318, part2 = "56,29")
        .warmup(10.seconds)
        .solveAll()
}
