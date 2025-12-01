package year2022

import aok.InputProvider
import aok.PuzzleInput
import aok.lines
import aoksp.AoKSolution
import aok.solveAll
import aok.warmup
import kotlin.math.abs
import kotlin.math.absoluteValue

//private const val WORLD_SIZE = 20
//fun main(): Unit = with(InputProvider.Example) {
private const val WORLD_SIZE = 4_000_000
fun main(): Unit = with(InputProvider) {
    queryDay(15).warmup(
        iterations = 3
    ).solveAll(
        runIterations = 1
    )
}

@AoKSolution
object Day15 {
    private data class Point(val x: Int, val y: Int)

    private infix fun Point.manhattanDistance(other: Point) = (x - other.x).absoluteValue + (y - other.y).absoluteValue
    private infix fun IntRange.overlaps(other: IntRange) =
        !other.isEmpty() && first <= other.last && other.first <= last

    private data class Report(val sensor: Point, val beacon: Point) {
        val range = sensor manhattanDistance beacon
    }

    private fun Report.coverage(row: Int) = (range - (row - sensor.y).absoluteValue)
        .let { if (it > 0) sensor.x - it..sensor.x + it else null }

    context(_: PuzzleInput)
    private fun parse() = lines.map {
        val (sx, sy, bx, by) = it.split('=', ',', ':').mapNotNull(String::toIntOrNull)
        Report(Point(sx, sy), Point(bx, by))
    }

    context(_: PuzzleInput)
    fun part1() = parse().let { reports ->
        val row = WORLD_SIZE / 2
        val objectsInRow = reports.objectsInRow(row)
        reports.sensorRanges(row).merge()
            .sumOf { range -> range.count() - objectsInRow.count { it in range } }
    }

    context(_: PuzzleInput)
    fun part2(): Long = parse().let { reports ->
        repeat(WORLD_SIZE) { row ->
            val all = reports.sensorRanges(row).merge()
            val inv = all.zipWithNext { a, b -> a.last + 1 until b.first }
            inv.singleOrNull { !it.isEmpty() }
                ?.let { if (it.first == it.last) return (it.first * 4_000_000L) + row }
        }
        error("No solution found")
    }

    private fun List<Report>.sensorRanges(row: Int) = mapNotNull { it.coverage(row) }

    private fun List<IntRange>.merge() = sortedBy { it.first }.fold(emptyList<IntRange>()) { ranges, range ->
        when {
            ranges.isEmpty() -> listOf(range)
            !(range overlaps ranges.last()) -> ranges + listOf(range)
            else -> ranges.take(ranges.size - 1) + ranges.last()
                .let { listOf(it.first..maxOf(it.last, range.last)) }
        }
    }

    private fun List<Report>.objectsInRow(row: Int) = buildSet {
        for ((s, b) in this@objectsInRow) {
            if (s.y == row) add(s.x)
            if (b.y == row) add(b.x)
        }
    }
}

@AoKSolution
object Day15Scanning {
    private data class Point(val x: Int, val y: Int) {
        fun manhattanDistance(ox: Int, oy: Int) = (x - ox).absoluteValue + (y - oy).absoluteValue
    }

    private data class Report(val sensor: Point, val beacon: Point) {
        val range = sensor.manhattanDistance(beacon.x, beacon.y)
        fun strengthAt(y: Int) = range - (y - sensor.y).absoluteValue
        fun inRange(x: Int, y: Int) = sensor.manhattanDistance(x, y) <= range
    }

    context(_: PuzzleInput)
    private fun parse() = lines.map {
        val (sx, sy, bx, by) = it.split('=', ',', ':').mapNotNull(String::toIntOrNull)
        Report(Point(sx, sy), Point(bx, by))
    }

    context(_: PuzzleInput)
    fun part1(row: Int = WORLD_SIZE / 2) = parse().let { reports ->
        reports.map { it.sensor.x to it.strengthAt(row) }
            .run { minOf { (x, s) -> x - s }..maxOf { (x, s) -> x + s } }
            .count { x -> reports.any { r -> r.inRange(x, row) && !(r.beacon.y == row && r.beacon.x == x) } }
    }

    context(_: PuzzleInput)
    fun part2(size: Int = WORLD_SIZE): Long = with(parse()) {
        repeat(size) { y ->
            var x = 0
            do {
                val next = find { it.inRange(x, y) } ?: return x * 4_000_000L + y
                x = next.sensor.x + next.strengthAt(y) + 1
            } while (x <= size)
        }
        error("no solution")
    }
}

@AoKSolution
object Day15BorderIntersection {
    private data class Point(val x: Int, val y: Int) {
        fun distance(ox: Int, oy: Int) = (x - ox).absoluteValue + (y - oy).absoluteValue
        fun shift(x: Int = 0, y: Int = 0) = Point(this.x + x, this.y + y)
    }

    context(_: PuzzleInput)
    private fun parse() = mutableSetOf<Point>().let { beacons ->
        lines.map {
            val (sx, sy, bx, by) = it.split('=', ',', ':').mapNotNull(String::toIntOrNull)
            beacons += Point(bx, by)
            Point(sx, sy).let { s -> s to s.distance(bx, by) }
        } to beacons.toSet()
    }

    context(_: PuzzleInput)
    fun part2(): Long {
        val (sensors) = parse()
        val lines = borderLines(sensors) + listOf(
            (Point(0, 0) to Point(0, WORLD_SIZE - 1)),
            (Point(0, WORLD_SIZE) to Point(WORLD_SIZE - 1, WORLD_SIZE)),
            (Point(WORLD_SIZE, WORLD_SIZE) to Point(WORLD_SIZE, 1)),
            (Point(WORLD_SIZE, 0) to Point(1, 0)),
        )

        val world = 0..WORLD_SIZE
        for (a in lines) for (b in lines) {
            val (x, y) = (a intersect b) ?: continue
            if (x in world && y in world && sensors.none { (s, r) -> s.distance(x, y) <= r })
                return x * 4_000_000L + y
        }
        error("no solution")
    }

    private fun borderLines(sensors: List<Pair<Point, Int>>) = sensors.flatMap { (sensor, range) ->
        val d = range + 1
        val t = sensor.shift(y = -d)
        val r = sensor.shift(x = d)
        val b = sensor.shift(y = d)
        val l = sensor.shift(x = -d)
        listOf(t to r, r to b, b to l, l to t)
    }

    private infix fun Pair<Point, Point>.intersect(line: Pair<Point, Point>): Point? {
        val (x1, y1) = first
        val (x2, y2) = second
        val (x3, y3) = line.first
        val (x4, y4) = line.second

        check(abs(x2 - x1) == abs(y2 - y1) || x1 == x2 || y1 == y2) { "not a square line" }
        check(abs(x4 - x3) == abs(y4 - y3) || x3 == x4 || y3 == y4) { "not a square line" }

        fun det(a: Point, b: Point) = a.x.toLong() * b.y - a.y.toLong() * b.x
        fun det(ax: Long, ay: Long, bx: Long, by: Long) = ax * by - ay * bx
        val xdx = (x1 - x2).toLong()
        val xdy = (x3 - x4).toLong()
        val ydx = (y1 - y2).toLong()
        val ydy = (y3 - y4).toLong()
        val div = det(xdx, xdy, ydx, ydy)
        if (div == 0L) return null

        val dx = det(first, second)
        val dy = det(line.first, line.second)
        val x = det(dx, dy, xdx, xdy) / div
        val y = det(dx, dy, ydx, ydy) / div
        return Point(x.toInt(), y.toInt())
    }
}
