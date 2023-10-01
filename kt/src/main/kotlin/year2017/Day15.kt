package year2017

import aok.PuzDSL
import aok.Warmup
import aoksp.AoKSolution
import arrow.core.Validated
import arrow.core.zip
import arrow.typeclasses.Semigroup
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day15 : PuzDSL({
    fun generator(seed: Int, factor: Long) = generateSequence(seed) { (it * factor).mod(Int.MAX_VALUE) }.drop(1)
    fun genA(seed: Int) = generator(seed, 16807)
    fun genB(seed: Int) = generator(seed, 48271)

    part1 {
        lines.map { it.split(' ').last().toInt() }
            .let { (a, b) -> genA(a) zip genB(b) }
            .take(40_000_000)
            .count { (a, b) -> a.and(0xFFFF) == b.and(0xFFFF) }
    }

    part2 {
        val (a, b) = lines.map { it.split(' ').last().toInt() }
        genA(a).filter { it % 4 == 0 }
            .zip(genB(b).filter { it % 8 == 0 })
            .take(5_000_000)
            .count { (a, b) -> a.and(0xFFFF) == b.and(0xFFFF) }
    }
})

fun main(): Unit = solveDay(
    15,
    warmup = Warmup.eachFor(5.seconds), runs = 3,
)
