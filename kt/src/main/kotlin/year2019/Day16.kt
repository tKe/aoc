package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import kotlin.math.absoluteValue

fun main() = solveDay(
    16,
//    input = aok.InputProvider.raw("03036732577212944063491565474664")
)

@AoKSolution
object Day16 : PuzDSL({
    val basePattern = listOf(0, 1, 0, -1)
    fun multiplier(digitIdx: Int, patternIdx: Int): Int {
        return basePattern[((patternIdx + 1) / (1 + digitIdx)).rem(basePattern.size)]
    }

    fun String.fft(skip: Int = 0) =
        CharArray(length) { digitIdx ->
            if (digitIdx >= skip)
                substring(skip).foldIndexed(0) { patternIndex, acc, c ->
                    acc + c.digitToInt() * multiplier(digitIdx, patternIndex + skip)
                }.absoluteValue.rem(10).digitToChar()
            else '0'
        }.concatToString()


    part1 {
        generateSequence(input, String::fft).elementAt(100).substring(0, 8)
    }

    part2 {
        val inputSignal = input.repeat(10000)
        val msgOffset = input.substring(0, 7).toInt()
        require(msgOffset > (inputSignal.length / 2)) { "not solved in the general case" }

        inputSignal.substring(msgOffset).map(Char::digitToInt).toIntArray().also {
            repeat(100) { _ ->
                for (idx in it.indices.reversed()) {
                    if (idx != it.lastIndex)
                        it[idx] += it[idx + 1]
                    it[idx] %= 10
                }
            }
        }.sliceArray(0..<8).joinToString("")

    }
})
