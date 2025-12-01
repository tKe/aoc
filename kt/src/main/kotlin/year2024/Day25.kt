package year2024

import aok.Parser
import aok.PuzzleInput
import aok.cached
import aok.checkAll
import aok.solveAll
import aok.warmup
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day25 {
    context(_: PuzzleInput) fun part1() = parse { (locks, keys) ->
        keys.sumOf { key ->
            locks.count { lock -> key fits lock }
        }
    }

    private infix fun Long.fits(other: Long) = and(other) == 0L

    val parse = Parser {
        input.splitToSequence("\n\n")
            .map {
                it.fold(0L) { acc, c ->
                    when (c) {
                        '#' -> acc shl 1 or 1
                        '.' -> acc shl 1
                        else -> acc
                    }
                }
            }
            .partition { it and 1 == 0L }
    }.cached()
}

fun main(): Unit = queryDay(25).run {
    checkAll(part1 = 3) {
        """
        #####
        .####
        .####
        .####
        .#.#.
        .#...
        .....
        
        #####
        ##.##
        .#.##
        ...##
        ...#.
        ...#.
        .....
        
        .....
        #....
        #....
        #...#
        #.#.#
        #.###
        #####
        
        .....
        .....
        #.#..
        ###..
        ###.#
        ###.#
        #####
        
        .....
        .....
        .....
        #....
        #.#..
        #.#.#
        #####
        """.trimIndent()
    }
    checkAll(part1 = 3466)
    warmup(10.seconds)
    solveAll(300)
}
