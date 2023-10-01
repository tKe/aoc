package year2017

import aok.PuzDSL
import aoksp.AoKSolution

typealias DanceMove = CharArray.() -> Unit

@AoKSolution
object Day16 : PuzDSL({
    fun spin(n: Int): DanceMove = {
        val p = size - n.mod(size)
        if (p != 0) {
            val a = sliceArray(0..<p)
            val b = sliceArray(p..lastIndex)
            System.arraycopy(a, 0, this, b.size, a.size)
            System.arraycopy(b, 0, this, 0, b.size)
        }
    }

    fun exchange(a: Int, b: Int): DanceMove = {
        val t = this[b]
        this[b] = this[a]
        this[a] = t
    }

    fun pair(a: Char, b: Char): DanceMove = {
        exchange(indexOf(a), indexOf(b))()
    }

    fun String.toMoves() = split(',').map {
        when {
            it.startsWith("s") -> {
                spin(it.substring(1).toInt())
            }

            it.startsWith("x") -> {
                val (a, b) = it.substring(1).split('/')
                exchange(a.toInt(), b.toInt())
            }

            it.startsWith("p") -> {
                val (a, b) = it.substring(1).split('/')
                pair(a.single(), b.single())
            }

            else -> TODO()
        }
    }

    infix fun CharArray.dance(moves: List<DanceMove>) = apply { moves.forEach { it(this) } }

    part1 {
        val dancers = ('a'..'p').toList().toCharArray()
        dancers dance input.toMoves()
        dancers.joinToString("")
    }

    part2 {
        val simCount = 1_000_000_000
        val moves = input.toMoves()
        val dance =
            generateSequence(('a'..'p').toList().toCharArray()) { it dance moves }
                .map(CharArray::concatToString)
                .takeWhile(mutableSetOf<String>()::add)
                .toList()
        dance[simCount % dance.size]
    }

})

fun main(): Unit = solveDay(
    16,
//    warmup = Warmup.eachFor(5.seconds), runs = 3,
)
