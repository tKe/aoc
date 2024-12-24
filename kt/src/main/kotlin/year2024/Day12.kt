package year2024

import aok.PuzzleInput
import aok.checkAll
import aok.solveAll
import aok.warmupEach
import aoksp.AoKSolution
import utils.forEachCharIndexed
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day12 {
    context(PuzzleInput) fun part1() = totalPrice(::price)
    context(PuzzleInput) fun part2() = totalPrice(::discountPrice)

    context(PuzzleInput) inline fun totalPrice(price: (Set<Point>) -> Int): Int {
        val farm = Farm(lines)
        var sum = 0
        val seen = mutableSetOf<Point>()
        farm.forEach { pt, c ->
            if (seen.add(pt)) {
                val region = pt.expand { farm[it] == c }
                seen.addAll(region)
                sum += price(region)
            }
        }
        return sum
    }

    fun price(region: Set<Point>) =
        region.size * region.sumOf { pt -> Direction.count { pt + it !in region } }

    fun discountPrice(region: Set<Point>): Int {
        val edges = buildSet {
            for (dir in Direction) for (pt in region) {
                if ((pt + dir) !in region) add(pt to dir)
            }
        }
        val sides = buildSet {
            for ((pos, dir) in edges) {
                if ((pos + dir.turnRight() to dir) !in edges)
                    add(pos to dir)
            }
        }.size
        return region.size * sides
    }

    inline fun Point.expand(predicate: (Point) -> Boolean) = buildSet {
        add(this@Point)
        val queue = mutableListOf(this@Point)
        while (queue.isNotEmpty()) {
            for (neighbour in queue.removeFirst().neighbours)
                if (predicate(neighbour) && add(neighbour))
                    queue += neighbour
        }
    }

    enum class Direction {
        U, R, D, L;

        fun turnRight() = entries[ordinal.inc() % entries.size]
        fun turnLeft() = entries[ordinal.dec().mod(entries.size)]

        companion object : Iterable<Direction> by entries
    }

    data class Point(val x: Int, val y: Int) {
        val neighbours by lazy { Direction.map(::plus) }

        operator fun plus(dir: Direction) = when (dir) {
            Direction.U -> copy(y = y - 1)
            Direction.D -> copy(y = y + 1)
            Direction.L -> copy(x = x - 1)
            Direction.R -> copy(x = x + 1)
        }

    }

    @JvmInline
    value class Farm(val lines: List<String>) {
        operator fun get(x: Int, y: Int) = lines.getOrNull(y)?.getOrNull(x) ?: '.'
        operator fun get(p: Point) = get(p.x, p.y)

        inline fun forEach(f: (pt: Point, c: Char) -> Unit) =
            lines.forEachCharIndexed { x, y, c -> f(Point(x, y), c) }
    }
}

@AoKSolution
object Day12FenceWalker {
    context(PuzzleInput) fun part1() = Day12.totalPrice(Day12::price)
    context(PuzzleInput) fun part2() = Day12.totalPrice(::discountPrice)

    private fun discountPrice(region: Set<Day12.Point>): Int {
        val edges = Day12.Direction.map { dir ->
            dir to mutableSetOf<Day12.Point>().apply {
                for (pt in region) if ((pt + dir) !in region) add(pt)
            }
        }
        var sides = 0
        for ((dir, pts) in edges) {
            val right = dir.turnRight()
            val left = dir.turnLeft()
            while (pts.isNotEmpty()) {
                val pt = pts.first().also(pts::remove)
                pt.moveWhile(left, pts::remove)
                pt.moveWhile(right, pts::remove)
                sides++
            }
        }
        return region.size * sides
    }

    private inline fun Day12.Point.moveWhile(dir: Day12.Direction, v: (Day12.Point) -> Boolean): Day12.Point {
        var at = this
        do at += dir while (v(at))
        return at
    }
}

fun main() {
    queryDay(12)
        .checkAll(
            1930, 1206,
            true, {
                """
                RRRRIICCFF
                RRRRIICCCF
                VVRRRCCFFF
                VVRCCCJFFF
                VVVVCJJCFE
                VVIVCCJJEE
                VVIIICJJEE
                MIIIIIJJEE
                MIIISIJEEE
                MMMISSJEEE
            """.trimIndent()
            })
        .checkAll(1381056, 834828)
        .warmupEach(10.seconds)
        .solveAll(30)
}