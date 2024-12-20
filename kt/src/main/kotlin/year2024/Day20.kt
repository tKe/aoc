package year2024

import aok.PuzzleInput
import aok.checkAll
import aok.solveAll
import aok.warmup
import aoksp.AoKSolution
import utils.bfsRoute
import utils.forEachCharIndexed
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day20 {
    context(PuzzleInput) fun part1() = solve(2)
    context(PuzzleInput) fun part2() = solve(20)

    private fun PuzzleInput.solve(cheatLength: Int): Int {
        val (start, end) = lines.ends()

        val path = buildList {
            var at = start
            var dir = Dir.entries.first { lines[at + it] != '#' }.right
            add(at)
            while(at != end) {
                dir = dir.left.takeIf { lines[at + it] != '#' } ?: dir.right
                while (lines[at + dir] != '#') {
                    at += dir
                    add(at)
                }
            }
        }

        var count = 0
        for (i in 0..(path.lastIndex - cheatLength)) {
            for (j in (i + cheatLength)..path.lastIndex) {
                val cost = path[i] distTo path[j]
                if (cost <= cheatLength) {
                    val saving = j - i - cost
                    if (saving >= 100) count++
                }
            }
        }
        return count
    }


    data class Pt(val x: Int, val y: Int) {
        infix fun distTo(pt: Pt) = (x - pt.x).absoluteValue + (y - pt.y).absoluteValue
    }

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

    operator fun List<String>.get(p: Pt) = getOrNull(p.y)?.getOrNull(p.x) ?: '#'

    private fun List<String>.ends(): Pair<Pt, Pt> {
        lateinit var start: Pt
        lateinit var end: Pt
        forEachCharIndexed { x, y, c ->
            when (c) {
                'S' -> start = Pt(x, y)
                'E' -> end = Pt(x, y)
            }
        }
        return start to end
    }
}

fun main() {
    queryDay(20)
//        .checkAll(
//            input = """
//                ###############
//                #...#...#.....#
//                #.#.#.#.#.###.#
//                #S#...#.#.#...#
//                #######.#.#.###
//                #######.#.#...#
//                #######.#.###.#
//                ###..E#...#...#
//                ###.#######.###
//                #...###...#...#
//                #.#####.#.###.#
//                #.#...#.#.#...#
//                #.#.#.#.#.#.###
//                #...#...#...###
//                ###############
//            """.trimIndent(),
//            part1 = 6,
//        )
        .checkAll(1426, 1000697)
        .warmup(10.seconds)
        .solveAll(3)
}
