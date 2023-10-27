package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.atan2

fun main() = solveDay(10,
//    input = aok.InputProvider.raw("""
//.#..##.###...#######
//##.############..##.
//.#.######.########.#
//.###.#######.####.#.
//#####.##.#.##.###.##
//..#####..#.#########
//####################
//#.####....###.#.#.##
//##.#################
//#####.##.###..####..
//..######..##.#######
//####.##.####...##..#
//.#####..#.######.###
//##...#.##########...
//#.##########.#######
//.####.#.###.###.#.##
//....##.##.###..#####
//.#.#.###########.###
//#.#.#.#####.####.###
//###.##.####.##.#..##
//    """.trimIndent())
)

@AoKSolution
object Day10 : PuzDSL({
    data class Int2(val x: Int, val y: Int) {
        operator fun minus(other: Int2) = Int2(x - other.x, y - other.y)
        operator fun plus(other: Int2) = Int2(x + other.x, y + other.y)
        operator fun div(scale: Int) = Int2(x / scale, y / scale)
    }

    val parseGrid = parser {
        buildSet {
            lines.forEachIndexed { y, s ->
                s.forEachIndexed { x, c ->
                    if (c == '#') add(Int2(x, y))
                }
            }
        }
    }

    tailrec fun gcd(a: Int, b: Int): Int = when {
        a == 0 -> b
        b == 0 -> a
        else -> gcd(minOf(a, b), maxOf(a, b) % minOf(a, b))
    }

    fun Int2.intermediatesTo(other: Int2): Sequence<Int2> {
        val vector = other - this
        val count = when {
            vector.x == 0 -> vector.y.absoluteValue
            vector.y == 0 -> vector.x.absoluteValue
            else -> gcd(vector.x.absoluteValue, vector.y.absoluteValue)
        }
        val step = vector / count
        return generateSequence(this, step::plus).drop(1).take(count - 1)
    }

    fun Set<Int2>.visibleFrom(loc: Int2) = count { b -> loc != b && loc.intermediatesTo(b).none(this::contains) }

    fun Int2.angle() = ((atan2(x.toFloat(), -y.toFloat()) / (PI/180)).mod(360.0))
    fun Int2.angleTo(other: Int2) = (other - this).angle()
    fun Int2.distTo(other: Int2) = (other - this).let { it.x.absoluteValue + it.y.absoluteValue }

    part1(parseGrid) { asteroids ->
        asteroids.maxOf(asteroids::visibleFrom)
    }


    part2(parseGrid) { asteroids ->
        val station = asteroids.maxBy(asteroids::visibleFrom)

        asteroids.asSequence()
            .filterNot(station::equals)
            .groupBy(station::angleTo)
            .flatMap { (angle, roids) -> roids.sortedBy(station::distTo).mapIndexed { index, roid -> angle + (index * 360) to roid } }
            .sortedBy { (angle, _) -> angle }
            .elementAt(199)
            .let { (_, roid) -> roid.x * 100 + roid.y }
    }
})
