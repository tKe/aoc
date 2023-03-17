package year2017

import aok.PuzDSL
import aoksp.AoKSolution
import kotlin.math.absoluteValue

@AoKSolution
object Day03 : PuzDSL({
    data class IntPair(val x: Int, val y: Int) {
        operator fun plus(other: IntPair) = IntPair(x + other.x, y + other.y)
    }

    val spiral by lazy {
        val directions = listOf(IntPair(1, 0), IntPair(0, -1), IntPair(-1, 0), IntPair(0, 1))
            .let { sequence { while (true) yieldAll(it) } } // R, U, L, D, R, U, L, D, ...
        val counts = generateSequence(1, Int::inc).flatMap { sequenceOf(it, it) } // 1, 1, 2, 2, 3, 3, 4, 4, ..., n, n
        val moves = directions.zip(counts) { m, c -> sequence { repeat(c) { yield(m) } } }.flatten()
        moves.runningFold(IntPair(0, 0), IntPair::plus)
    }

    part1 {
        val (x, y) = spiral.elementAt(input.trim().toInt() - 1)
        x.absoluteValue + y.absoluteValue
    }

    val stressSpiral = sequence {
        val directions = (-1..1).flatMap { x -> (-1..1).map { IntPair(x, it) } }.minus(IntPair(0, 0)).asSequence()
        fun IntPair.neighbours() = directions.map(this::plus)
        val storage = mutableMapOf(IntPair(0, 0) to 1)

        yield(1)
        for(loc in spiral.drop(1)) {
            val v = loc.neighbours().mapNotNull(storage::get).sum()
            storage[loc] = v
            yield(v)
        }
    }

    part2 {
        val n = input.trim().toInt()
        stressSpiral.first { it > n }
    }
})

fun main(): Unit = solveDay(3)
