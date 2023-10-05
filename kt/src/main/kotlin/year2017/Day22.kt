package year2017

import aok.InputProvider
import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution
import year2017.Day22.Direction.*
import year2017.Day22.State.*

@AoKSolution
object Day22 : PuzDSL({
    data class Node(val x: Int, val y: Int)

    infix fun Node.move(direction: Direction) = when (direction) {
        Left -> copy(x = x - 1)
        Right -> copy(x = x + 1)
        Up -> copy(y = y - 1)
        Down -> copy(y = y + 1)
    }

    fun Direction.turnLeft() = when (this) {
        Up -> Left
        Left -> Down
        Down -> Right
        Right -> Up
    }

    fun Direction.turnRight() = when (this) {
        Up -> Right
        Right -> Down
        Down -> Left
        Left -> Up
    }

    fun PuzzleInput.loadInfections(): Set<Node> = buildSet {
        val cy = lines.size / 2
        val cx = lines.first().length / 2
        lines.forEachIndexed { y, s ->
            s.forEachIndexed { x, c ->
                if (c == '#') add(Node(x - cx, y - cy))
            }
        }
    }

    part1 {
        var infectionsCaused = 0
        val infections = loadInfections().toMutableSet()
        var loc = Node(0, 0)
        var dir = Up

        repeat(10000) {
            if (loc in infections) {
                dir = dir.turnRight()
                infections -= loc
            } else {
                dir = dir.turnLeft()
                infections += loc
                infectionsCaused++
            }
            loc = loc move dir
        }
        infectionsCaused
    }
    part2 {
        var infectionsCaused = 0
        val nodes = loadInfections().associateWith { Infected }.toMutableMap()
        var loc = Node(0, 0)
        var dir = Up

        repeat(10_000_000) {
            val state = nodes[loc] ?: Clean
            dir = when(state) {
                Clean -> dir.turnLeft()
                Weak -> dir
                Infected -> dir.turnRight()
                Flagged -> dir.turnLeft().turnLeft()
            }
            when(state) {
                Clean -> nodes[loc] = Weak
                Weak -> {
                    nodes[loc] = Infected
                    infectionsCaused++
                }
                Infected -> nodes[loc] = Flagged
                Flagged -> nodes -= loc
            }
            loc = loc move dir
        }
        infectionsCaused
    }
}) {
    private enum class Direction { Left, Right, Up, Down }
    private enum class State { Clean, Weak, Infected, Flagged }
}

fun main(): Unit = solveDay(
    22,
//    input = aok.InputProvider.raw(
//        """
//            ..#
//            #..
//            ...
//        """.trimIndent()
//    )
)
