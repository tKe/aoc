package year2020

import aok.PuzDSL
import aoksp.AoKSolution
import year2020.Day12.Direction.*

fun main() = solveDay(
    12,
//    input = aok.InputProvider.Example
)

@AoKSolution
object Day12 : PuzDSL({

    part1 {
        data class Ship(val loc: Int2 = Int2(0, 0), val d: Direction = E)
        lines.fold(Ship()) { ship, instruction ->
            val n = instruction.drop(1).toInt()
            when (instruction.first()) {
                'N' -> ship.copy(loc = ship.loc.move(N, n))
                'E' -> ship.copy(loc = ship.loc.move(E, n))
                'S' -> ship.copy(loc = ship.loc.move(S, n))
                'W' -> ship.copy(loc = ship.loc.move(W, n))
                'F' -> ship.copy(loc = ship.loc.move(ship.d, n))
                'L' -> ship.copy(d = ship.d.rotate(-n))
                'R' -> ship.copy(d = ship.d.rotate(n))
                else -> error("unhandled instruction '$instruction'")
            }
        }.let { (loc) -> loc.x + loc.y }
    }

    part2 {
        data class Ship(val loc: Int2 = Int2(0, 0), val waypoint: Int2 = Int2(10, -1))
        lines.fold(Ship()) { ship, instruction ->
            val n = instruction.drop(1).toInt()
            when (instruction.first()) {
                'N' -> ship.copy(waypoint = ship.waypoint.move(N, n))
                'E' -> ship.copy(waypoint = ship.waypoint.move(E, n))
                'S' -> ship.copy(waypoint = ship.waypoint.move(S, n))
                'W' -> ship.copy(waypoint = ship.waypoint.move(W, n))
                'F' -> ship.copy(loc = ship.loc + ship.waypoint * n)
                'L' -> ship.copy(waypoint = ship.waypoint.rotate(-n))
                'R' -> ship.copy(waypoint = ship.waypoint.rotate(n))
                else -> error("unhandled instruction '$instruction'")
            }
        }.let { (loc) -> loc.x + loc.y }
    }

}) {
    enum class Direction {
        N, E, S, W;

        fun rotate(degrees: Int) = entries[(ordinal + degrees.mod(360) / 90).mod(entries.size)]
    }

    data class Int2(val x: Int, val y: Int) {
        fun move(d: Direction, n: Int = 1) = when (d) {
            N -> copy(y = y - n)
            E -> copy(x = x + n)
            S -> copy(y = y + n)
            W -> copy(x = x - n)
        }

        operator fun plus(other: Int2) = Int2(x + other.x, y + other.y)
        operator fun times(scale: Int) = Int2(x * scale, y * scale)
        fun rotate(degrees: Int) = when (degrees.mod(360)) {
            0 -> this
            90 -> copy(y = x, x = -y)
            180 -> copy(x = -x, y = -y)
            270 -> copy(y = -x, x = y)
            else -> error("unsupported rotation $degrees°")
        }
    }

}

