package year2023

import aok.LineParser
import aok.PuzDSL
import aoksp.AoKSolution
import utils.splitIntsNotNull
import year2023.Day22.GROUND
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

fun main() = solveDay(22)

@AoKSolution
object Day22 : PuzDSL({
    fun List<Brick>.supports(): Array<Set<Int>> {
        val floor = Array(10 * 10) { 0 to GROUND }
        return Array(size) { brickIdx ->
            val brick = this@supports[brickIdx]
            val floors = brick.xy.map(floor::get)
            val supportedAt = floors.maxOf { it.first }
            floors.filter { it.first == supportedAt }.map { it.second }.toSet().also {
                val top = supportedAt + brick.height to brickIdx
                for (xy in brick.xy) floor[xy] = top
            }
        }
    }

    part1(Day22.parseBricks) { bricks ->
        val structural = bricks.supports().mapNotNullTo(mutableSetOf(), Set<Int>::singleOrNull).minus(GROUND)
        bricks.size - structural.size
    }

    part2(Day22.parseBricks) { bricks ->
        val supports = bricks.supports()
        fun impact(brickIdx: Int) = buildSet {
            add(brickIdx)
            do {
                var removed = 0
                for (brick in bricks.indices) if (brick !in this && containsAll(supports[brick])) {
                    add(brick)
                    removed++
                }
            } while (removed > 0)
        }.size - 1
        bricks.indices.sumOf(::impact)
    }
}) {
    const val GROUND = -1
    data class Int3(val x: Int, val y: Int, val z: Int)
    data class Brick(val a: Int3, val b: Int3)  {
        val xy by lazy {
            buildList {
                for (y in min(a.y, b.y)..max(a.y, b.y))
                    for (x in min(a.x, b.x)..max(a.x, b.x))
                        add(y * 10 + x)
            }
        }
        val height = (1 + b.z - a.z).absoluteValue
    }

    private val parseBricks = LineParser {
        it.split("~")
            .map { it.splitIntsNotNull(",") }
            .map { (x, y, z) -> Int3(x, y, z) }.sortedBy(Int3::z)
            .let { (a, b) -> Brick(a, b) }
    }.andThen { it.sortedBy { b -> b.a.z } }

}
