package year2020

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(17)

@AoKSolution
object Day17 : PuzDSL({
    part1({ ConwayGrid(lines, ::Int3) }) { grid ->
        generateSequence(grid) { it.evolve() }.elementAt(6).count
    }
    part2({ ConwayGrid(lines, ::Int4) }) { grid ->
        generateSequence(grid) { it.evolve() }.elementAt(6).count
    }
}) {
    @JvmInline
    value class ConwayGrid<T : Neighboured<T>>(private val active: Set<T>) {
        val count get() = active.size

        constructor(lines: List<String>, init: (x: Int, y: Int) -> T) : this(buildSet {
            for ((y, line) in lines.withIndex())
                for ((x, c) in line.withIndex())
                    if (c == '#') add(init(x, y))
        })

        fun evolve() = ConwayGrid(buildSet<T> {
            val pending = mutableSetOf<T>()
            for (cell in active) {
                val (activeNeighbours, inactiveNeighbours) = cell.neighbours.partition(active::contains)
                if (activeNeighbours.size in 2..3) add(cell)
                pending += inactiveNeighbours
            }
            pending.filterTo(this) { it.neighbours.count(active::contains) == 3 }
        })
    }

    interface Neighboured<T : Neighboured<T>> {
        val neighbours: Sequence<T>
    }

    data class Int3(val x: Int, val y: Int, val z: Int = 0) : Neighboured<Int3> {
        override val neighbours = sequence {
            for (dx in -1..1)
                for (dy in -1..1)
                    for (dz in -1..1)
                        if (dx != 0 || dy != 0 || dz != 0)
                            yield(Int3(x + dx, y + dy, z + dz))
        }
    }

    data class Int4(val x: Int, val y: Int, val z: Int = 0, val w: Int = 0) : Neighboured<Int4> {
        override val neighbours = sequence {
            for (dx in -1..1)
                for (dy in -1..1)
                    for (dz in -1..1)
                        for (dw in -1..1)
                            if (dx != 0 || dy != 0 || dz != 0 || dw != 0)
                                yield(Int4(x + dx, y + dy, z + dz, w + dw))
        }
    }
}