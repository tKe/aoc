package year2018

import aok.PuzDSL
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds

fun main(): Unit = solveDay(
    12,
//    warmup = aok.Warmup.eachFor(3.seconds), runs = 300,
//    input = aok.InputProvider.raw(
//        """
//        initial state: #..#.#..##......###...###
//
//        ...## => #
//        ..#.. => #
//        .#... => #
//        .#.#. => #
//        .#.## => #
//        .##.. => #
//        .#### => #
//        #.#.# => #
//        #.### => #
//        ##.#. => #
//        ##.## => #
//        ###.. => #
//        ###.# => #
//        ####. => #
//        """.trimIndent()
//    ),
)

@AoKSolution
object Day12 : PuzDSL({
    fun String.foldInt(bit: (Char) -> Boolean) =
        fold(0) { acc, it -> acc.shl(1) or if (bit(it)) 1 else 0 }

    val parse = parser {
        val (initial, evolutions) = input.split("\n\n")
        val state = initial.substringAfterLast(' ')
            .mapIndexedNotNull { idx, c -> idx.takeIf { c == '#' } }
            .toSet()
        state to buildSet {
            evolutions.lineSequence().filter { it.endsWith(" => #") }
                .forEach {
                    val (pattern) = it.split(" => ")
                    add(pattern.foldInt('#'::equals))
                }
        }
    }

    fun Set<Int>.evolveBy(evolutions: Set<Int>) = generateSequence(this) { plants ->
        buildSet {
            var key = 0
            for(pot in plants.min()-2..plants.max() + 2) {
                // shift our key and add the i+2'th pot
                key = key.shl(1).or(if (pot+2 in plants) 1 else 0).and(0b11111)
                if (key in evolutions) add(pot)
            }
        }
    }.takeWhile { it.isNotEmpty() }

    part1 {
        val (state, evolutions) = parse()
        state.evolveBy(evolutions).elementAt(20).sum()
    }

    part2 {
        val (state, evolutions) = parse()
        val sums = state.evolveBy(evolutions).map { it.sum() }

        // assumes stability is reached the first time we get the same delta of plants
        val generations = 50_000_000_000L
        var lastDelta = 0
        var lastSum = 0
        var run = 0
        for ((idx, sum) in sums.withIndex()) {
            val delta = sum - lastSum
            println(delta)
            if (lastDelta == delta) {
                run++
                if(run == 100)
                return@part2 sum + (generations - idx) * delta
            }
            lastSum = sum
            lastDelta = delta
        }
        error("no stability")
    }
})
