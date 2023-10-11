package year2018

import aok.PuzDSL
import aoksp.AoKSolution
import year2018.Day13.Direction.*
import year2018.Day13.invoke
import kotlin.time.Duration.Companion.seconds

fun main(): Unit = solveDay(
    13,
//    warmup = aok.Warmup.eachFor(3.seconds), runs = 10,
//    input = aok.InputProvider.raw(
//        """
//            /->-\
//            |   |  /----\
//            | /-+--+-\  |
//            | | |  | v  |
//            \-+-/  \-+--/
//              \------/
//        """.trimIndent()
//    ),
//    input = aok.InputProvider.raw(
//        """
//            />-<\
//            |   |
//            | /<+-\
//            | | | v
//            \>+</ |
//              |   ^
//              \<->/
//        """.trimIndent()
//    ),
)

@AoKSolution
object Day13 : PuzDSL({

    val parse = parser {
        val carts = mutableListOf<Cart>()
        val track = lines.mapIndexed { y, row ->
            row.mapIndexed { x, c ->
                when (c) {
                    '^' -> carts.add(Cart(x, y, Up)).let { '|' }
                    'v' -> carts.add(Cart(x, y, Down)).let { '|' }
                    '<' -> carts.add(Cart(x, y, Left)).let { '-' }
                    '>' -> carts.add(Cart(x, y, Right)).let { '-' }
                    else -> c
                }
            }.toCharArray()
        }.toTypedArray()

        Track(track) to carts.toList()
    }

    fun Cart.moveOn(track: Track): Cart {
        if (hasCrashed) return this
        var (x, y) = this
        when (dir) {
            Up -> y--
            Down -> y++
            Left -> x--
            Right -> x++
        }
        return when (track[x, y]) {
            '-', '|' -> copy(x = x, y = y)
            '+' -> copy(x = x, y = y, dir = dir(turn), turn = turn.next)
            '/' -> copy(
                x = x, y = y, dir = when (dir) {
                    Up, Down -> dir.right
                    Left, Right -> dir.left
                }
            )

            '\\' -> copy(
                x = x, y = y, dir = when (dir) {
                    Up, Down -> dir.left
                    Left, Right -> dir.right
                }
            )

            else -> error("invalid location")
        }
    }

    fun MutableList<Cart>.pop(x: Int, y: Int) =
        find { it.x == x && it.y == y && !it.hasCrashed }?.also { remove(it) }

    fun List<Cart>.moveOn(track: Track, keepCrashed: Boolean = true) =
        sortedWith(Cart.moveOrder).toMutableList().let { unmoved ->
            buildList {
                while (unmoved.isNotEmpty()) {
                    val cart = unmoved.removeFirst().moveOn(track)
                    val collision = pop(cart.x, cart.y) ?: unmoved.pop(cart.x, cart.y)
                    if (collision == null) add(cart)
                    else if (keepCrashed) {
                        add(collision.copy(hasCrashed = true))
                        add(cart.copy(hasCrashed = true))
                    }
                }
            }
        }

    part1 {
        val (track, initialCarts) = parse()

        generateSequence(initialCarts) { it.moveOn(track) }
            .firstNotNullOf { it.find { it.hasCrashed } }.run { "$x,$y" }
    }

    part2 {
        val (track, initialCarts) = parse()

        generateSequence(initialCarts) { it.moveOn(track, keepCrashed = false) }
            .firstOrNull { it.count { !it.hasCrashed } <= 1 }
            ?.singleOrNull()?.run { "$x,$y" }
    }
}) {
    enum class Direction {
        Up, Down, Left, Right;

        val left by lazy {
            when (this) {
                Up -> Left
                Down -> Right
                Left -> Down
                Right -> Up
            }
        }

        val right by lazy {
            when (this) {
                Up -> Right
                Down -> Left
                Left -> Up
                Right -> Down
            }
        }
    }

    enum class Turn {
        TurnLeft, Straight, TurnRight;

        fun applyTo(direction: Direction) = when (this) {
            TurnLeft -> direction.left
            Straight -> direction
            TurnRight -> direction.right
        }

        val next by lazy { values().let { it[(ordinal + 1) % it.size] } }
    }

    operator fun Direction.invoke(turn: Turn) = turn.applyTo(this)


    data class Cart(
        val x: Int,
        val y: Int,
        val dir: Direction,
        val turn: Turn = Turn.TurnLeft,
        val hasCrashed: Boolean = false,
    ) {
        companion object {
            val moveOrder = compareBy<Cart> { it.y }.thenBy { it.x }
        }
    }

    @JvmInline
    value class Track(private val layout: Array<CharArray>) {
        operator fun get(x: Int, y: Int) = layout.getOrNull(y)?.getOrNull(x) ?: ' '

        fun debug(carts: List<Cart>, boxChars: String = "─│┼╭╯╰╮ " /*"═║╬╔╝╚╗"*/) = with(layout) {
            fun cart(x: Int, y: Int) = carts.find { it.x == x && it.y == y }

            for (y in indices) {
                val r = this[y]
                for (x in r.indices) {
                    val c = r[x]
                    print(cart(x, y)?.let {
                        if (it.hasCrashed) 'X'
                        else when (it.dir) {
                            Up -> '^'
                            Down -> 'v'
                            Left -> '<'
                            Right -> '>'
                        }
                    } ?: when (c) {
                        '-' -> 0
                        '|' -> 1
                        '+' -> 2
                        '/' -> when (r.getOrNull(x + 1)) {
                            '-', '+' -> 3
                            else -> 4
                        }

                        '\\' -> when (r.getOrNull(x + 1)) {
                            '-', '+' -> 5
                            else -> 6
                        }

                        else -> null
                    }?.let(boxChars::getOrNull) ?: ' ')
                }
                println()
            }
        }
    }
}
