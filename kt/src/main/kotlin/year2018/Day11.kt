package year2018

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds

fun main(): Unit = solveDay(
    11,
    warmup = aok.Warmup.eachFor(3.seconds), runs = 3,
//    input = aok.InputProvider.raw("42"),
)

@AoKSolution
object Day11 : PuzDSL({

    fun powerCalculator(serialNumber: Int): (x: Int, y: Int) -> Int = { x, y ->
        // Find the fuel cell's rack ID, which is its X coordinate plus 10.
        val rackId = x + 10
        // Begin with a power level of the rack ID times the Y coordinate.
        var powerLevel = (rackId * y)
        // Increase the power level by the value of the grid serial number (your puzzle input).
        powerLevel += serialNumber
        // Set the power level to itself multiplied by the rack ID.
        powerLevel *= rackId
        // Keep only the hundreds digit of the power level (so 12345 becomes 3; numbers with no hundreds digit become 0).
        powerLevel = (powerLevel / 100) % 10
        // Subtract 5 from the power level.
        powerLevel - 5
    }

    fun PuzzleInput.powerGrid(serialNumber: Int = input.toInt()) = SumGrid(300, powerCalculator(serialNumber))

    part1 {
        val powerGrid = powerGrid()

        var max = 0
        var coord = ""
        val size = 3
        val r = 1..300 - (size - 1)
        for (x in r) for (y in r) {
            val sum = powerGrid[x, y, size]
            if (sum > max) {
                max = sum
                coord = "$x,$y"
            }
        }

        coord
    }

    part2 {
        val power = powerCalculator(input.toInt())
        val sumGrid = SumGrid(300, power)

        var max = 0
        var coord = ""
        for (size in 1..300) {
            val r = 1..300 - (size - 1)
            for (y in r) for (x in r) {
                val sum = sumGrid[x, y, size]
                if (sum > max) {
                    max = sum
                    coord = "$x,$y,$size"
                }
            }
        }

        coord
    }
}) {
    class SumGrid(private val size: Int, source: (x: Int, y: Int) -> Int) {
        val data: IntArray = IntArray(size * size).also {
            var i = 0
            for (y in 1..300) for (x in 1..300) {
                it[i] = source(x, y) + when {
                    x == 1 && y == 1 -> 0
                    x == 1 -> it[i - size]
                    y == 1 -> it[i - 1]
                    else -> it[i - size] + it[i - 1] - it[i - 1 - size]
                }
                i++
            }
        }

        operator fun get(x: Int, y: Int, size: Int): Int {
            // indices are bottom right index of each quadrant, given x,y are 1-based
            // a | b
            // --+--
            // c | d
            val a = (y - 2) * this.size + (x - 2)
            val b = a + size
            val c = a + (size * this.size)
            val d = c + size

            return when {
                x == 1 && y == 1 -> data[d]
                x == 1 -> data[d] - data[b] // no a or c when x == 1
                y == 1 -> data[d] - data[c] // no a or b when x == 1
                else -> data[d] - data[c] - data[b] + data[a]
            }
        }
    }
}

