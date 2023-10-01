package year2017

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

@AoKSolution
object Day17 : PuzDSL({
    part1 {
        val steps = input.toInt()
        var i = 0
        val l = mutableListOf(0)
        (1..2017).forEach {
            i += steps
            i %= l.size
            l.add(++i, it)
        }
        l[i+1 % l.size]
    }

    part2 {
        val steps = input.toInt()
        var i = 0
        (1..50_000_000).last {
            i = 1 + (i + steps) % it
            i == 1
        }
    }

})

fun main(): Unit = solveDay(
    17,
//    warmup = Warmup.eachFor(5.seconds), runs = 3,
) { _, _ -> PuzzleInput.of("304") }
