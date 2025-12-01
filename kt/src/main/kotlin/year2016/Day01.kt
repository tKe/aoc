package year2016

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution
import arrow.core.compose
import kotlin.math.absoluteValue

@AoKSolution
object Day01 : PuzDSL({
    data class Location(val x: Int, val y: Int, val facing: Char = 'N')

    fun Location.turn(dir: Char) = when (facing) {
        'N' -> copy(facing = if (dir == 'L') 'W' else 'E')
        'W' -> copy(facing = if (dir == 'L') 'S' else 'N')
        'S' -> copy(facing = if (dir == 'L') 'E' else 'W')
        'E' -> copy(facing = if (dir == 'L') 'N' else 'S')
        else -> error("invalid direction")
    }

    fun Location.walk(blocks: Int) = when (facing) {
        'N' -> copy(y = y - blocks)
        'S' -> copy(y = y + blocks)
        'E' -> copy(x = x + blocks)
        'W' -> copy(x = x - blocks)
        else -> error("invalid direction")
    }

    fun Location.distance() = x.absoluteValue + y.absoluteValue

    fun PuzzleInput.walk() = sequence {
        var loc = Location(0, 0)
        for (s in input.trim().split(", ")) {
            loc = loc.turn(s[0])
            repeat(s.substring(1).toInt()) {
                loc = loc.walk(1).also { yield(it) }
            }
        }
    }

    part1 {
        walk()
            .onEach(::println)
            .last().distance()
    }

    part2 {
        walk()
            .onEach(::println)
            .dropWhile(mutableSetOf<Pair<Int, Int>>()::add.compose { it.x to it.y })
            .first().distance()
    }
})

fun main() = solveDay(1)
