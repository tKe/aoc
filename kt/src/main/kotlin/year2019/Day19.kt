package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import year2019.Day09.IntcodeProgram

fun main() = solveDay(
    19,
)

@AoKSolution
object Day19 : PuzDSL({

    operator fun IntcodeProgram.get(x: Int, y: Int) = load().run {
        send(x, y)
        receive() == 1L
    }


    part1(IntcodeProgram) { droneSystem ->
        var count = 0
        for (y in 0..<50) for (x in 0..<50) if (droneSystem[x, y]) count++
        count
    }

    part2(IntcodeProgram) { ds ->
        with(object {
            var leftX: Int = 0
            var topY: Int = 0
            val rightX get() = leftX + 99
            val bottomY get() = topY + 99
        }) {
            // shift the box down until the top right is in the beam
            while (!ds[rightX, topY]) {
                topY++
                // shift the box right until the bottom left is in the beam
                while (!ds[leftX, bottomY]) leftX++
            }
            10000 * leftX + topY
        }
    }
})
