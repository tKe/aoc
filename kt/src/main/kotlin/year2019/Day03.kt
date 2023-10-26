package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import kotlin.math.absoluteValue

fun main(): Unit = solveDay(
        3,
)

@AoKSolution
object Day03 : PuzDSL({
    data class Int2(val x: Int, val y: Int)

    val parser = lineParser { line ->
        line.split(",").map { it[0] to it.substring(1).toInt() }
    }

    fun Iterable<Pair<Char, Int>>.route() = sequence {
        var i = Int2(0, 0)
        forEach { (dir, count) ->
            repeat(count) {
                i = when(dir) {
                    'U' -> i.copy(y = i.y - 1)
                    'D' -> i.copy(y = i.y + 1)
                    'L' -> i.copy(x = i.x - 1)
                    'R' -> i.copy(x = i.x + 1)
                    else -> error("invalid direction '$dir'")
                }.also { yield(it) }
            }
        }
    }

    part1(parser) { (a, b) ->
        val intersects = a.route().toSet() intersect b.route().toSet()
        intersects.minOf { it.y.absoluteValue + it.x.absoluteValue }
    }

    part2(parser) { (a, b) ->
        val intersects = a.route().toSet() intersect b.route().toSet()
        val aRoute = a.route().toList()
        val bRoute = b.route().toList()

        2 + intersects.minOf { aRoute.indexOf(it) + bRoute.indexOf(it) }
    }
})
