@file:OptIn(ExperimentalCoroutinesApi::class)

package year2017

import aok.PuzDSL
import aoksp.AoKSolution
import kotlinx.coroutines.ExperimentalCoroutinesApi
import year2017.Day19.Dir.*
import year2017.Day19.char
import year2017.Day19.route

@AoKSolution
object Day19 : PuzDSL({
    part1 {
        with(lines) {
            route()
                .mapNotNull { it.char.takeIf(Char::isLetter) }.joinToString("")
        }
    }
    part2 {
        lines.route().count()
    }
}) {

    val List<String>.start get() = Pos(x = first().indexOf('|'), y = 0)
    fun List<String>.route() = generateSequence(start) { it.move() }
    enum class Dir(val dx: Int = 0, val dy: Int = 0, val char: Char) {
        DOWN(dy = 1, char = '|'), UP(dy = -1, char = '|'), LEFT(dx = -1, char = '-'), RIGHT(dx = 1, char = '-')
    }

    data class Pos(val x: Int, val y: Int, val dir: Dir = DOWN) {
        fun move(dir: Dir) = Pos(x + dir.dx, y + dir.dy, dir)

        context(List<String>) fun move(): Pos? =
            when (char) {
                // have to turn
                '+' -> when (dir) {
                    DOWN, UP -> listOf(LEFT, RIGHT)
                    LEFT, RIGHT -> listOf(DOWN, UP)
                }.map(::move).first { it.char.let { c -> c == it.dir.char || c.isLetter() } }

                else -> move(dir).takeUnless { it.char == ' ' }
            }

    }

    context(List<String>) val Pos.char get() = getOrElse(y) { "" }.getOrElse(x) { ' ' }

    context(T)
    fun <T> get() = this
}

fun main(): Unit = solveDay(
    19,
//    warmup = Warmup.eachFor(5.seconds), runs = 3,
//    input = InputProvider.raw(
//        """
//         |    |
//         |    |  +--+
//         |    A  |  C
//         |F---|----E|--+
//         |    |  |  |  D
//         |    +B-+  +--+
//        """.trimMargin()
//    )
)
