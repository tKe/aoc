package year2018

import aok.PuzDSL
import aoksp.AoKSolution
import utils.splitIntsNotNull

fun main(): Unit = solveDay(
    10,
)

@AoKSolution
object Day10 : PuzDSL({

    fun <T> Iterable<T>.rangeOf(extract: (T) -> Int) = minOf(extract)..maxOf(extract)

    data class Int2(val x: Int, val y: Int) {
        operator fun plus(other: Int2) = Int2(x + other.x, y + other.y)
    }

    data class Light(val pos: Int2, val vel: Int2) {
        fun step() = copy(pos = pos + vel)
    }

    val parse = lineParser {
        it.splitIntsNotNull("<", ">", ",", "=", " ").let { (px, py, vx, vy) ->
            Light(Int2(px, py), Int2(vx, vy))
        }
    }

    fun List<Light>.simulate() = generateSequence(this@simulate) { lights -> lights.map(Light::step) }.map {
        buildSet { it.forEach { (pos) -> add(pos) } }
    }

    fun Set<Int2>.dump() {
        val xRange = rangeOf(Int2::x)
        for (y in rangeOf(Int2::y)) {
            for (x in xRange) {
                print(if (Int2(x, y) in this) "🟡" else "⚫️")
            }
            println()
        }
    }

    fun Set<Int2>.height() = rangeOf(Int2::y).run { 1 + last - first }

    part1 {
        parse().simulate().map { it to it.height() }
            .zipWithNext { (a, ah), (_, bh) -> a.takeIf { ah < bh } }
            .firstNotNullOf { it }.dump()
    }

    part2 {
        parse().simulate().map { it to it.height() }
            .zipWithNext { (a, ah), (_, bh) -> a.takeIf { ah < bh } }
            .indexOfFirst { it != null }
    }
})

