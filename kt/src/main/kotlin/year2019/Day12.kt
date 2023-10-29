package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import utils.lcm
import utils.splitIntsNotNull
import kotlin.math.absoluteValue
import kotlin.reflect.KMutableProperty1

fun main() = solveDay(
    12,
//    input = aok.InputProvider.raw(
//        """
//        <x=-8, y=-10, z=0>
//        <x=5, y=5, z=10>
//        <x=2, y=-7, z=3>
//        <x=9, y=-8, z=-3>
//        """.trimIndent()
//    )
)

@AoKSolution
object Day12 : PuzDSL({
    data class Int3(var x: Int, var y: Int, var z: Int) {
        val mag get() = x.absoluteValue + y.absoluteValue + z.absoluteValue

        operator fun plusAssign(other: Int3) {
            x += other.x
            y += other.y
            z += other.z
        }
    }

    class Moon(val pos: Int3, val vel: Int3 = Int3(0, 0, 0))

    val parse = lineParser {
        val (x, y, z) = it.splitIntsNotNull("<", ">", "=", ", ")
        Moon(Int3(x, y, z))
    }

    fun KMutableProperty1<Int3, Int>.applyGravity(a: Moon, b: Moon) {
        if (get(a.pos) > get(b.pos)) {
            set(a.vel, get(a.vel) - 1)
            set(b.vel, get(b.vel) + 1)
        }
        if (get(a.pos) < get(b.pos)) {
            set(a.vel, get(a.vel) + 1)
            set(b.vel, get(b.vel) - 1)
        }
    }

    fun <T> List<T>.forEachPair(f: (a: T, b: T) -> Unit) {
        forEachIndexed { index, a ->
            for (b in (index + 1)..lastIndex) f(a, get(b))
        }
    }

    part1(parse) { moons ->
        repeat(1000) {
            moons.forEachPair { a, b ->
                Int3::x.applyGravity(a, b)
                Int3::y.applyGravity(a, b)
                Int3::z.applyGravity(a, b)
            }
            moons.forEach { it.pos += it.vel }
        }
        moons.sumOf { it.pos.mag * it.vel.mag }
    }

    part2(parse) { moons ->
        fun List<Int>.cycleLength(): Long {
            val pos = toIntArray()
            val vel = IntArray(pos.size) { 0 }
            var count = 0L
            while (true) {
                indices.forEach { a ->
                    for (b in (a + 1)..lastIndex) {
                        if(pos[a] > pos[b]) {
                            vel[a]--
                            vel[b]++
                        }
                        if(pos[a] < pos[b]) {
                            vel[a]++
                            vel[b]--
                        }
                    }
                }
                indices.forEach { pos[it] += vel[it] }
                count++
                if(vel.all { it == 0 } && pos.toList()==this) break
            }
            return count
        }

        val xCycle = moons.map { it.pos.x  }.cycleLength()
        val yCycle = moons.map { it.pos.y  }.cycleLength()
        val zCycle = moons.map { it.pos.z  }.cycleLength()
        lcm(xCycle, lcm(yCycle, zCycle))
    }
})
