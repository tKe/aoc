package year2023

import aok.PuzDSL
import aoksp.AoKSolution
import kotlin.math.max

fun main() = solveDay(
    2
)

@AoKSolution
object Day02 : PuzDSL({
    data class Game(val id: Int, val shown: List<Map<String, Int>>)

    val parser = lineParser { line ->
        val parts = line.split(": ", "; ")
        val id = parts.first().substringAfter(' ').toInt()
        Game(id, parts.drop(1).map { part ->
            part.split(", ").associate {
                it.substringAfter(' ') to it.substringBefore(' ').toInt()
            }
        })
    }

    part1(parser) { games ->
        val limits = mapOf("red" to 12, "green" to 13, "blue" to 14)
        games
            .filter { game ->
                game.shown.all { it.all { (k, v) -> v <= (limits[k] ?: 0) } }
            }
            .sumOf { it.id }
    }

    part2(parser) { games ->
        fun Game.minimumBag() = buildMap {
            for ((k, v) in shown.flatMap { it.entries }) {
                compute(k) { _, o -> max(o ?: 0, v) }
            }
        }
        fun Map<String, Int>.power() = values.fold(1, Int::times)

        games.sumOf { it.minimumBag().power() }
    }
})