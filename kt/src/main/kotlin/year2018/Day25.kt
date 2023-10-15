package year2018

import aok.PuzDSL
import aoksp.AoKSolution
import utils.splitIntsNotNull
import kotlin.math.absoluteValue

fun main(): Unit = solveDay(
    25,
//    input = aok.InputProvider.Example
)

@AoKSolution
object Day25 : PuzDSL({
    data class Int4(val x: Int, val y: Int, val z: Int, val t: Int)

    val parser = lineParser {
        it.splitIntsNotNull(",")
            .let { (x, y, z, t) -> Int4(x, y, z, t) }
    }

    infix fun Int4.distTo(o: Int4) =
        (o.x - x).absoluteValue + (o.y - y).absoluteValue + (o.z - z).absoluteValue + (o.t - t).absoluteValue

    part1 {
        val coords = parser()

        val links = coords.associateWithTo(mutableMapOf()) {
            coords.filterTo(mutableSetOf()) { b -> it distTo b <= 3 }
        }

        var count = 0
        val current = mutableSetOf<Int4>()
        while (links.isNotEmpty()) {
            if (current.isEmpty()) {
                current += links.keys.first()
                count++
            }
            val toLink = current.intersect(links.keys)
            if (toLink.isEmpty()) current.clear()
            else for (key in toLink) current += links.remove(key).orEmpty()
        }
        count
    }
})
