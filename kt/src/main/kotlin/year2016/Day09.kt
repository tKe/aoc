package year2016

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day09 : PuzDSL({
    fun CharIterator.readInt() = asSequence().takeWhile(Char::isDigit)
        .fold(0) { a, c -> a * 10 + c.digitToInt() }

    fun String.decompressedLength(formatVersion: Int = 1): Long {
        var sum = 0L
        val chars = iterator()
        while(chars.hasNext()) {
            when(chars.nextChar()) {
                '(' -> {
                    val length = chars.readInt()
                    val count = chars.readInt()
                    val segment = chars.asSequence().take(length)

                    sum += count * when (formatVersion) {
                        1 -> segment.count().toLong()
                        2 -> segment.joinToString("").decompressedLength(formatVersion)
                        else -> error("unsupported format version $formatVersion")
                    }
                }
                else -> sum++
            }
        }
        return sum
    }

    part1 { input.trim().decompressedLength() }
    part2 { input.trim().decompressedLength(formatVersion = 2) }
})

fun main() = solveDay(
    9,
//    warmup = Warmup.eachFor(3.seconds)
//    input = InputProvider.raw("(25x3)(3x3)ABC(2x3)XY(5x2)PQRSTX(18x9)(3x2)TWO(5x7)SEVEN")
)
