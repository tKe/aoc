package year2016

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day16 : PuzDSL({
    fun String.parseData() = trim().let { BooleanArray(it.length) { idx -> it[idx] == '1' } }

    fun BooleanArray.applyCurve() = copyOf(1 + 2 * size)
        .also { forEachIndexed { idx, b -> it[it.lastIndex - idx] = !b } }

    fun BooleanArray.fillDisk(size: Int) = generateSequence(this, BooleanArray::applyCurve)
        .first { it.size >= size }
        .copyOf(size)

    fun BooleanArray.checksum() = generateSequence(this) { prev ->
        if(prev.size % 2 == 0) BooleanArray(prev.size / 2) {
            prev[2 * it] == prev[2 * it + 1]
        }
        else null
    }.last().joinToString("") { if(it) "1" else "0" }

    part1 {
        input.parseData().fillDisk(272).checksum().also { check(it == "10100011010101011") }
    }

    part2 {
        input.parseData().fillDisk(35651584).checksum().also { check(it == "01010001101011001") }
    }
})

fun main() = solveDay(
    16,
//    input = InputProvider.raw("10000")
)
