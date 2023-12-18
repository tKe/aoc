package year2023

import aok.PuzDSL
import aoksp.AoKSolution
import kotlin.math.absoluteValue

fun main() = solveDay(
    18,
    warmup = aok.Warmup.iterations(10_000), runs = 300,
)

@AoKSolution
object Day18 : PuzDSL({
    fun vertices(extract: (String) -> Int2) = lineParser(extract).map { it.runningReduce(Int2::plus) }

    fun areaPerimeter(route: List<Int2>) = (route + route.take(2))
        .windowed(3) { (a, b, c) -> b.y.toLong() * (a.x - c.x) to (a manhDist b) }
        .fold(0L to 0L) { (sa, sp), (a, p) -> sa + a to sp + p }
        .let { (a, p) -> a.absoluteValue / 2 + p / 2 + 1 }

    part1(vertices {
        it.split(" ").let { (dir, dist) -> Direction.valueOf(dir) * dist.toInt() }
    }, ::areaPerimeter)

    part2(vertices {
        it.substringAfter('#').take(6).toInt(16).let { c -> Direction.entries[c and 0xF] * c.ushr(4) }
    }, ::areaPerimeter)
}) {
    data class Int2(val x: Int, val y: Int) {
        operator fun plus(other: Int2) = Int2(x = x + other.x, y = y + other.y)
        infix fun manhDist(other: Int2) = (x - other.x).absoluteValue + (y - other.y).absoluteValue
    }

    enum class Direction(val dx: Int = 0, val dy: Int = 0) {
        R(dx = 1), D(dy = 1), L(dx = -1), U(dy = -1);

        operator fun times(n: Int) = Int2(dx * n, dy * n)
    }
}
