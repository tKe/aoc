package year2018

import aok.PuzDSL
import aoksp.AoKSolution
import kotlin.math.absoluteValue

@AoKSolution
object Day06 : PuzDSL({
    data class Loc(val x: Int, val y: Int, val name: String? = null) {
        infix fun distanceTo(other: Loc) = (x - other.x).absoluteValue + (y - other.y).absoluteValue
        override fun toString() = name ?: "($x,$y)"
    }

    data class Box(val left: Int, val top: Int, val right: Int, val bottom: Int) : Iterable<Loc> {
        operator fun contains(loc: Loc) =
            loc.x in left..right && loc.y in top..bottom

        override fun iterator() = iterator {
            for (y in top..bottom)
                for (x in left..right)
                    yield(Loc(x, y))
        }

        val perimeter = sequence {
            for (x in left..right) {
                yield(Loc(x, top))
                yield(Loc(x, bottom))
            }
            for (y in top + 1..<bottom) {
                yield(Loc(left, y))
                yield(Loc(right, y))
            }
        }
    }

    fun List<Loc>.bounds() = Box(minOf { it.x }, minOf { it.y }, maxOf { it.x }, maxOf { it.y })
    fun Loc.closestOfOrNull(locs: List<Loc>): Loc? {
        val minDist = locs.minOf(::distanceTo)
        return locs.singleOrNull { minDist == distanceTo(it) }
    }

    fun List<Loc>.closestToOrNull(other: Loc) = other.closestOfOrNull(this)

    val parseLocs = parser {
        lines.mapIndexed { i, s ->
            val (x, y) = s.split(", ").map(String::toInt)
            Loc(x, y, ('A' + i).toString())
        }
    }

    part1 {
        val locs = parseLocs()
        val bounds = locs.bounds()
        val infinites = bounds.perimeter.mapNotNull(locs::closestToOrNull).toSet()
        bounds
            .groupingBy(locs::closestToOrNull).eachCount()
            .filterKeys { it != null && it !in infinites }
            .maxOf { it.value }
    }

    part2 {
        val locs = parseLocs()
        locs.bounds().count { locs.sumOf(it::distanceTo) < 10_000 }
    }
})

fun main(): Unit = solveDay(
    6,
//    input = aok.InputProvider.raw(
//        """
//        1, 1
//        1, 6
//        8, 3
//        3, 4
//        5, 5
//        8, 9
//    """.trimIndent()
//    ),
)
