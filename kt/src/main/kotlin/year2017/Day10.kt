package year2017

import aok.PuzDSL
import aoksp.AoKSolution
import java.util.HexFormat

@AoKSolution
object Day10 : PuzDSL({
    fun <T> List<T>.rotateBy(n: Int): List<T> = object : AbstractList<T>() {
        override val size = this@rotateBy.size
        override fun get(index: Int) = this@rotateBy[(index + n).mod(size)]
    }

    fun <T> List<T>.twist(start: Int, length: Int) =
        with(rotateBy(start)) { (subList(0, length).asReversed() + drop(length)) }.rotateBy(-start)

    fun <T> List<T>.knot(lengths: List<Int>) = lengths
        .foldIndexed(this to 0) { skip, (list, ofs), len ->
            list.twist(ofs, len) to (ofs + len + skip)
        }
        .first

    operator fun <T> List<T>.times(n: Int) = List(n) { this }.flatten()

    fun String.knotHash() = List(256) { it }
        .knot((toByteArray().map(Byte::toInt) + listOf(17, 31, 73, 47, 23)) * 64)
        .chunked(16) { c -> c.reduce(Int::xor).toByte() }
        .toByteArray()

    part1 {
        val lengths = input.trim().split(',').mapNotNull(String::toIntOrNull)
        List(256) { it }.knot(lengths).let { (a, b) -> a * b }
    }

    part2 {
        input.trim().knotHash().let(HexFormat.of()::formatHex)
    }
})


fun main(): Unit = solveDay(
    10,
//    input = InputProvider.raw("1,2,3")
)
