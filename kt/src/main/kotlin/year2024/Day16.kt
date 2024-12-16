package year2024

import aok.PuzzleInput
import aok.checkAll
import aok.solveAll
import aok.warmup
import aoksp.AoKSolution
import utils.forEachCharIndexed
import java.util.PriorityQueue
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day16 {
    data class Pt(val x: Int, val y: Int)
    enum class Dir {
        N, E, S, W;

        val left by lazy { entries[(ordinal + 3) % entries.size] }
        val right by lazy { entries[(ordinal + 1) % entries.size] }
    }

    operator fun Pt.plus(dir: Dir) = when (dir) {
        Dir.N -> Pt(x, y - 1)
        Dir.E -> Pt(x + 1, y)
        Dir.S -> Pt(x, y + 1)
        Dir.W -> Pt(x - 1, y)
    }

    operator fun List<String>.get(p: Pt) = this.getOrNull(p.y)?.getOrNull(p.x) ?: '#'

    context(PuzzleInput) fun part1(): Int {
        lateinit var start: Pt
        lateinit var end: Pt
        lines.forEachCharIndexed { x, y, c ->
            when (c) {
                'S' -> start = Pt(x, y)
                'E' -> end = Pt(x, y)
            }
        }

        data class Reindeer(val at: Pt = start, val facing: Dir = Dir.E)

        val seen = mutableSetOf<Reindeer>()
        val queue = PriorityQueue<Pair<Reindeer, Int>>(compareBy { it.second }).also { it += Reindeer() to 0 }

        fun Reindeer.enqueue(move: Dir, score: Int) {
            val next = Reindeer(at + move, move)
            if (next !in seen && lines[next.at] != '#')
                queue.add(next to score)
        }

        while (queue.isNotEmpty()) {
            val (reindeer, score) = queue.remove()
            if (reindeer.at == end) return score
            if (!seen.add(reindeer)) continue
            reindeer.enqueue(reindeer.facing, score + 1)
            reindeer.enqueue(reindeer.facing.left, score + 1001)
            reindeer.enqueue(reindeer.facing.right, score + 1001)
        }
        return -1
    }

    context(PuzzleInput) fun part2(): Int {
        lateinit var start: Pt
        lateinit var end: Pt
        lines.forEachCharIndexed { x, y, c ->
            when (c) {
                'S' -> start = Pt(x, y)
                'E' -> end = Pt(x, y)
            }
        }

        var bestScore = Int.MAX_VALUE

        data class Reindeer(val at: Pt = start, val facing: Dir = Dir.E)

        val seen = mutableSetOf<Reindeer>()
        val queue =
            PriorityQueue<Triple<Reindeer, Int, Reindeer>>(compareBy { it.second }).also {
                it += Triple(Reindeer(), 0, Reindeer())
            }

        fun Reindeer.enqueue(move: Dir, score: Int) {
            if (score <= bestScore) {
                val next = Reindeer(at + move, move)
                if (next !in seen && lines[next.at] != '#')
                    queue.add(Triple(next, score, this))
            }
        }

        val routeTo = mutableMapOf<Reindeer, MutableSet<Reindeer>>()
        while (queue.isNotEmpty()) {
            val (reindeer, score, route) = queue.remove()
            queue.removeIf { it.first == reindeer && it.third == route }

            routeTo.getOrPut(reindeer, ::mutableSetOf).add(route)
            seen += reindeer
            if (reindeer.at == end) {
                if(score < bestScore) {
                    bestScore = score
                    queue.removeIf { it.second > bestScore }
                }
                continue
            }
            reindeer.enqueue(reindeer.facing, score + 1)
            reindeer.enqueue(reindeer.facing.left, score + 1001)
            reindeer.enqueue(reindeer.facing.right, score + 1001)
        }

        return buildSet {
            val pending = routeTo.keys.filterTo(ArrayDeque()) { it.at == end }
            while (pending.isNotEmpty()) {
                val next = pending.removeFirst()
                if (add(next))
                    pending += routeTo[next].orEmpty()
            }
        }.distinctBy { it.at }.size
    }
}

fun main() {
    queryDay(16)
        .checkAll(
            input = """
                ###############
                #.......#....E#
                #.#.###.#.###.#
                #.....#.#...#.#
                #.###.#####.#.#
                #.#.#.......#.#
                #.#.#####.###.#
                #...........#.#
                ###.#.#####.#.#
                #...#.....#.#.#
                #.#.#.###.#.#.#
                #.....#...#.#.#
                #.###.#.#.#.#.#
                #S..#.....#...#
                ###############
            """.trimIndent(), part1 = 7036, part2 = 45
        )
        .checkAll(
            input = """
                #################
                #...#...#...#..E#
                #.#.#.#.#.#.#.#.#
                #.#.#.#...#...#.#
                #.#.#.#.###.#.#.#
                #...#.#.#.....#.#
                #.#.#.#.#.#####.#
                #.#...#.#.#.....#
                #.#.#####.#.###.#
                #.#.#.......#...#
                #.#.###.#####.###
                #.#.#...#.....#.#
                #.#.#.#####.###.#
                #.#.#.........#.#
                #.#.#.#########.#
                #S#.............#
                #################
            """.trimIndent(), part1 = 11048, part2 = 64
        )
        .checkAll()
        .warmup(10.seconds)
        .solveAll(3)
}