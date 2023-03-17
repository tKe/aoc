package year2016

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

@AoKSolution
object Day08 : PuzDSL({

    fun <T> List<T>.rotate(n: Int) = List(size) { get((it - n).mod(size)) }
    class Screen {
        private val pixels = BooleanArray(50 * 6)

        operator fun get(x: Int, y: Int) = pixels[y * 50 + x]
        operator fun set(x: Int, y: Int, value: Boolean) {
            pixels[y * 50 + x] = value
        }

        fun rect(width: Int, height: Int) = repeat(height) { y ->
            repeat(width) { x ->
                set(x, y, true)
            }
        }

        private fun Iterable<Int>.rotate(n: Int) {
            zip(map(pixels::get).rotate(n), pixels::set)
        }

        fun rotateRow(y: Int, n: Int) =
            ((y * 50) until ((y + 1) * 50)).rotate(n)

        fun rotateCol(x: Int, n: Int) =
            (x..pixels.lastIndex step 50).rotate(n)

        fun display() = pixels.map { if (it) "🟢" else "⚫" }
            .chunked(50)
            .joinToString("\n") { it.joinToString("") }

        fun countLit() = pixels.count { it }
    }

    fun List<String>.drawTo(screen: Screen) = with(screen) {
        forEach {
            val (a, b) = it.split(' ', '=', 'x').mapNotNull(String::toIntOrNull)
            if (it[1] == 'e') rect(a, b)
            else if (it[7] == 'c') rotateCol(a, b)
            else rotateRow(a, b)
        }
    }

    part1 { Screen().apply(lines::drawTo).countLit() }
    part2 { Screen().apply(lines::drawTo).display() }
})

fun main() = solveDay(
    8,
)
