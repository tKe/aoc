package year2023

import aok.PuzDSL
import aoksp.AoKSolution
import year2023.Day16.Direction.*

fun main() = solveDay(16)

@AoKSolution
object Day16 : PuzDSL({
    fun List<String>.tile(beam: Beam) = getOrNull(beam.y)?.getOrNull(beam.x)
    fun List<String>.cast(start: Beam = Beam(0, 0, Right), onSplit: (Beam) -> Unit) = sequence {
        var beam = start
        val visited = mutableSetOf<Beam>()
        while (true) {
            val tile = tile(beam) ?: break
            if (!visited.add(beam)) break
            yield(beam.x to beam.y)
            beam = beam.handle(tile).also { (_, split) ->
                split?.let { onSplit(it) }
            }.first
        }
    }

    fun List<String>.energize(
        start: Beam = Beam(0, 0, Right),
    ): Int = buildSet {
        val beams = mutableSetOf(start)
        val pending = ArrayDeque(beams)
        while (pending.isNotEmpty()) {
            addAll(cast(pending.removeFirst()) { if (beams.add(it)) pending += it })
        }
    }.count()

    part1 {
        lines.energize()
    }

    part2 {
        sequence {
            val yr = lines.indices
            val xr = lines.first().indices
            for (y in yr) {
                yield(lines.energize(Beam(xr.first, y, Right)))
                yield(lines.energize(Beam(xr.last, y, Left)))
            }
            for (x in xr) {
                yield(lines.energize(Beam(x, yr.first, Down)))
                yield(lines.energize(Beam(x, yr.last, Up)))
            }
        }.max()
    }
}) {
    enum class Direction {
        Up, Right, Down, Left;

        val isVertical by lazy { this == Up || this == Down }
        val turnLeft by lazy { entries[ordinal.dec().mod(entries.size)] }
        val turnRight by lazy { entries[ordinal.inc().mod(entries.size)] }
    }

    data class Beam(val x: Int, val y: Int, val d: Direction) {
        fun move() = when (d) {
            Up -> copy(y = y - 1)
            Down -> copy(y = y + 1)
            Left -> copy(x = x - 1)
            Right -> copy(x = x + 1)
        }

        fun handle(tile: Char) = when (tile to d.isVertical) {
            '|' to false, '-' to true -> copy(d = d.turnLeft).move() to copy(d = d.turnRight).move()
            '/' to true, '\\' to false -> copy(d = d.turnRight).move() to null
            '/' to false, '\\' to true -> copy(d = d.turnLeft).move() to null
            else -> move() to null
        }
    }
}
