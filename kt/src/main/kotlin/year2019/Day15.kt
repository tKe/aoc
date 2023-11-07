package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import arrow.core.andThen
import year2019.Day09.IntcodeCpu
import year2019.Day09.IntcodeProgram
import year2019.Day15.Direction.*

fun main() = solveDay(
    15,
    warmup = aok.Warmup.iterations(25),
    runs = 25
)

@AoKSolution
object Day15 : PuzDSL({

    fun IntcodeCpu.move(d: Direction): Cell {
        send(d.ordinal + 1L)
        return Cell.entries[receive().toInt()]
    }

    data class Droid(
        val snapshot: IntcodeCpu.Snapshot,
        val loc: Int2 = Int2(0, 0),
    )

    val initDroid = IntcodeProgram.andThen { Droid(it.load().snapshot()) }

    part1(initDroid) { droid ->
        val visited = mutableSetOf(Int2(0, 0))
        val openStates = ArrayDeque<Pair<Droid, Int>>()
        openStates += droid to 1
        while (openStates.isNotEmpty()) {
            val (current, dist) = openStates.removeFirst()
            for ((dir, neighbour) in Direction.entries.associateWith(current.loc::move).filterValues(visited::add)) {
                val cpu = current.snapshot.fork()
                val cell = cpu.move(dir)
                if (cell == Cell.Oxygen) return@part1 dist
                else if (cell == Cell.Open) openStates += Droid(cpu.snapshot(), neighbour) to dist + 1
            }
        }
    }

    part2(initDroid) { droid ->
        val shipMap = buildMap {
            val openStates = ArrayDeque(listOf(droid))
            put(droid.loc, Cell.Open)
            while (openStates.isNotEmpty()) {
                val current = openStates.removeFirst()
                for (dir in Direction.entries) {
                    val neighbour = current.loc.move(dir)
                    if (!containsKey(neighbour)) {
                        val cpu = current.snapshot.fork()
                        val cell = cpu.move(dir)
                        put(neighbour, cell)
                        if (cell != Cell.Wall) openStates += Droid(cpu.snapshot(), current.loc.move(dir))
                    }
                }
            }
        }

        var count = 0
        val frontier = shipMap.filterValues { it == Cell.Oxygen }.keys.toMutableSet()
        val vacant = shipMap.filterValues { it == Cell.Open }.keys.toMutableSet()
        while (vacant.isNotEmpty()) {
            frontier += frontier.flatMap { Direction.entries.map(it::move) }
            frontier.retainAll(vacant::remove)
            count++
        }
        count
    }
}) {
    enum class Cell { Wall, Open, Oxygen }
    enum class Direction { North, South, West, East }
    data class Int2(val x: Int, val y: Int) {
        fun move(dir: Direction) = when (dir) {
            North -> copy(y = y - 1)
            South -> copy(y = y + 1)
            West -> copy(x = x - 1)
            East -> copy(x = x + 1)
        }
    }
}
