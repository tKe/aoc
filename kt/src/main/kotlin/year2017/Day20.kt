@file:OptIn(ExperimentalCoroutinesApi::class)

package year2017

import aok.PuzDSL
import aoksp.AoKSolution
import kotlinx.coroutines.ExperimentalCoroutinesApi
import year2017.Day19.Dir.*
import kotlin.math.absoluteValue

@AoKSolution
object Day20 : PuzDSL({
    part1 {
        generateSequence(lines.mapIndexed(Particle::fromString)) { it.map(Particle::simulate) }
            .drop(1000)
            .map { it.minBy { (_, p) -> p.manDist }.id }
            .windowed(100, 1, transform = List<Int>::toSet)
            .first { it.size == 1 }
            .single()
    }
    part2 {
        generateSequence(lines.mapIndexed(Particle::fromString)) {
            it.map(Particle::simulate)
                .groupBy { (_, p) -> p }
                .mapNotNull { (_, value) -> value.singleOrNull() }
        }
            .map { it.size }
            .windowed(20, 1, transform = List<Int>::toSet)
            .first { it.size == 1 }
            .single()
    }
}) {
    data class Int3(val x: Int, val y: Int, val z: Int) {
        val manDist by lazy { x.absoluteValue + y.absoluteValue + z.absoluteValue }
        operator fun plus(other: Int3) = Int3(x + other.x, y + other.y, z + other.z)
    }

    data class Particle(val id: Int, val p: Int3, val v: Int3, val a: Int3) {
        fun simulate() = (v + a).let { v -> copy(p = p + v, v = v) }

        companion object {
            fun fromString(id: Int, str: String) = str
                .split('<', ',', '>')
                .mapNotNull(String::toIntOrNull)
                .chunked(3) { (x, y, z) -> Int3(x, y, z) }
                .let { (p, v, a) -> Particle(id, p, v, a) }
        }
    }
}

fun main(): Unit = solveDay(20)
