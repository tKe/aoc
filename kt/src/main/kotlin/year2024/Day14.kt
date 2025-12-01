package year2024

import aok.PuzzleInput
import aok.checkAll
import aok.lines
import aok.solveAll
import aok.warmup
import aoksp.AoKSolution
import utils.splitIntsNotNull
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day14 {
    context(_: PuzzleInput) fun part1() = robotsInSpace { robots ->
        robots.roam { seconds, positions -> if (seconds == 100) return@robotsInSpace positions.safetyScore() }
    }

    context(_: PuzzleInput) fun part2() = robotsInSpace { robots ->
        robots.roam { seconds, positions -> if (positions.isChristmasTree()) return@robotsInSpace seconds }
    }

    context(space: Space)
    private fun List<Pt>.safetyScore() =
        groupingBy { (x, y) -> x.compareTo(space.width / 2) to y.compareTo(space.height / 2) }.eachCount()
            .filterKeys { (qx, qy) -> qx != 0 && qy != 0 }
            .values.reduce(Int::times)

    private fun List<Pt>.isChristmasTree(): Boolean {
        val positions = toSet()
        return positions.any { (x, y) ->
            for (dy in 0..4)
                for (dx in -dy..dy)
                    if (Pt(x + dx, y + dy) !in positions)
                        return@any false
            return@any true
        }
    }

    context(_: PuzzleInput)
    private fun robots() = lines.map {
        it.splitIntsNotNull("=", ",", " ")
            .let { (px, py, vx, vy) -> Robot(Pt(px, py), Pt(vx, vy)) }
    }

    private fun List<Robot>.requiredSpace() = when (maxOf { maxOf(it.p.x, it.p.y) }) {
        in 0..13 -> Space.Example
        else -> Space.Real
    }

    context(_: PuzzleInput)
    private inline fun <R> robotsInSpace(block: Space.(List<Robot>) -> R): R =
        robots().let { it.requiredSpace().run { block(it) } }

    private enum class Space(val width: Int, val height: Int) {
        Example(11, 7),
        Real(101, 103),
    }

    data class Pt(val x: Int, val y: Int)

    private data class Robot(var p: Pt, val v: Pt)

    context(space: Space)
    private operator fun Pt.plus(v: Pt) = Pt(x = (x + v.x).mod(space.width), y = (y + v.y).mod(space.height))

    context(_: Space)
    private inline fun List<Robot>.roam(v: (Int, List<Pt>) -> Unit): Nothing {
        var seconds = 0
        val positions = map { it.p }.toMutableList()
        while (true) {
            v(seconds++, positions)
            for (i in indices) positions[i] += get(i).v
        }
    }
}

fun main() {
    queryDay(14)
        .checkAll(12) {
            """
            p=0,4 v=3,-3
            p=6,3 v=-1,-3
            p=10,3 v=-1,2
            p=2,0 v=2,-1
            p=0,0 v=1,3
            p=3,0 v=-2,-2
            p=7,6 v=-1,-3
            p=3,0 v=-1,-2
            p=9,3 v=2,3
            p=7,3 v=-1,2
            p=2,4 v=2,-3
            p=9,5 v=-3,-3
            """.trimIndent()
        }
        .checkAll(part1 = 225648864, part2 = 7847)
        .warmup(5.seconds)
        .solveAll(5)
}
