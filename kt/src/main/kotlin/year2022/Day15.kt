package year2022

import InputScopeProvider
import aok.PuzzleInput
import aoksp.AoKSolution
import queryPuzzles
import solveAll
import kotlin.math.absoluteValue

private const val WORLD_SPACE = 4_000_000
fun main(): Unit = with(InputScopeProvider) {
    queryPuzzles { year == 2022 && day == 15 }.solveAll(
        warmupIterations = 5, runIterations = 1
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

    context(PuzzleInput)
    private fun parse() = lines.map {
        val (sx, sy, bx, by) = it.split('=', ',', ':').mapNotNull(String::toIntOrNull)
        Report(Point(sx, sy), Point(bx, by))
    }

    context(PuzzleInput)
    fun part1() = parse().let { reports ->
        val row = WORLD_SPACE / 2
        val objectsInRow = reports.objectsInRow(row)
        reports.sensorRanges(row).merge()
            .sumOf { range -> range.count() - objectsInRow.count { it in range } }
    }

    context(PuzzleInput)
    fun part2(): Long = parse().let { reports ->
        repeat(WORLD_SPACE) { row ->
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

    context(PuzzleInput)
    private fun parse() = lines.map {
        val (sx, sy, bx, by) = it.split('=', ',', ':').mapNotNull(String::toIntOrNull)
        Report(Point(sx, sy), Point(bx, by))
    }

    context(PuzzleInput)
    fun part1(row: Int = WORLD_SPACE / 2) = parse().let { reports ->
        reports.map { it.sensor.x to it.strengthAt(row) }
            .run { minOf { (x, s) -> x - s }..maxOf { (x, s) -> x + s } }
            .count { x -> reports.any { r -> r.inRange(x, row) && !(r.beacon.y == row && r.beacon.x == x) } }
    }

    context(PuzzleInput)
    fun part2(size: Int = WORLD_SPACE): Long = with(parse()) {
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
