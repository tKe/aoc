package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import year2019.Day09.IntcodeProgram

fun main() = solveDay(11)

@AoKSolution
object Day11 : PuzDSL({
    suspend fun IntcodeProgram.paint(initialWhite: Boolean = false) = coroutineScope {
        val (i, o) = launch()

        var location = Int2(0, 0)
        var direction = Direction.Up

        buildMap {
            if(initialWhite) set(location, true)
            while (isActive) {
                i.send(if (get(location) == true) 1 else 0)
                val paint = o.receiveCatching().getOrNull() ?: break
                set(location, paint == 1L)
                direction = if (o.receive() == 0L) direction.turnLeft else direction.turnRight
                location = location move direction
            }
        }
    }

    part1(IntcodeProgram) { prog ->
        prog.paint().size
    }

    part2(IntcodeProgram) { prog ->
        val painted = prog.paint(initialWhite = true)
        val white = painted.filterValues { it }.keys

        val ys = white.minOf(Int2::y)..white.maxOf(Int2::y)
        val xs = white.minOf(Int2::x)..white.maxOf(Int2::x)
        for(y in ys) println(xs.joinToString("") { if (Int2(it, y) in white) "⚪️" else "⚫️" })
    }
}) {
    data class Int2(val x: Int, val y: Int) {
        infix fun move(direction: Direction) = when (direction) {
            Direction.Up -> copy(y = y - 1)
            Direction.Down -> copy(y = y + 1)
            Direction.Left -> copy(x = x - 1)
            Direction.Right -> copy(x = x + 1)
        }
    }

    enum class Direction {
        Up, Down, Left, Right;

        val turnLeft by lazy {
            when (this) {
                Up -> Left
                Left -> Down
                Down -> Right
                Right -> Up
            }
        }
        val turnRight by lazy {
            when (this) {
                Left -> Up
                Down -> Left
                Right -> Down
                Up -> Right
            }
        }
    }
}
