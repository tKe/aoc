package year2018

import aok.PuzDSL
import aoksp.AoKSolution
import utils.splitIntsNotNull
import kotlin.math.absoluteValue

fun main(): Unit = solveDay(23)

@AoKSolution
object Day23 : PuzDSL({
    data class Int3(val x: Int, val y: Int, val z: Int) {
        infix fun mdistTo(other: Int3) = (x - other.x).absoluteValue + (y - other.y).absoluteValue + (z - other.z).absoluteValue
    }
    data class NanoBot(val pos: Int3, val range: Int)

    val parser = lineParser {
        val (x, y,z, r) = it.splitIntsNotNull("<", ">", ",", "=")
        NanoBot(Int3(x, y, z), r)
    }

    part1 {
        val nanobots = parser()
        val maxBot = nanobots.maxBy { it.range }
        nanobots.count { maxBot.pos mdistTo it.pos <= maxBot.range }
    }

    part2 {
        // didn't expect this to work...
        // finds the range of distances (ignoring actual position) from origin to each
        // bot signal, then finds the starting distance that's contained within the
        // most distance ranges. If the nanobots weren't so clustered together this wouldn't work.
        val nanobots = parser()
        val origin = Int3(0,0,0)
        val ranges = nanobots.map { (pos, range) ->
            val mid = pos mdistTo origin
            maxOf(0, mid - range)..mid + range
        }
        ranges.maxBy { ranges.count { r -> it.first in r } }.first
    }
})

