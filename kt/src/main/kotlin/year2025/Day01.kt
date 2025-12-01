package year2025

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day01 : PuzDSL({
    part1 {
        var dial = 50
        input.lineSequence().count {
            val dir = it[0]
            var count = it.drop(1).toInt()
            if(dir == 'L') count *= -1
            dial = (dial + count) % 100
            dial == 0
        }
    }

    part2 {
        var dial = 50
        var password = 0
        input.lineSequence().forEach {
            val dir = it[0]
            var count = it.drop(1).toInt()
            val click = if(dir == 'L') -1 else 1
            repeat(count) {
                dial = (dial + click) % 100
                if (dial == 0) password++
            }
        }
        password
    }
})

fun main() = solveDay(1)
