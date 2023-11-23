package year2019

import aok.LineParser
import aok.PuzDSL
import aoksp.AoKSolution
import year2019.Day22.parseShuffle

fun main() = solveDay(
    22,
    warmup = aok.Warmup.iterations(300),
    runs = 30,
//    input = aok.InputProvider.Example
)

@AoKSolution
object Day22 : PuzDSL({

    part1(parseShuffle(10007)) { shuffle ->
        shuffle.positionOf(2019)
    }

    part2(parseShuffle(119_315_717_514_047)) { shuffle ->
        (shuffle * 101_741_582_076_661L).cardAt(2020L)
    }
}) {
    fun parseShuffle(cards: Long) = LineParser {
        when {
            it == "deal into new stack" -> Technique.stack(cards)
            it.startsWith("cut ") -> Technique.cut(it.split(' ')[1].toLong(), cards)
            it.startsWith("deal with increment ") -> Technique.deal(it.substringAfterLast(' ').toLong(), cards)
            else -> error("unknown technique: $it")
        }
    }.map { it.reduce(Technique::compose) }

    private inline fun <T> reduceByDoubling(identity: T, initial: T, n: Long, reduce: (T, T) -> T): T {
        var remaining = n
        var current = initial
        var result = identity
        while (remaining > 0) {
            if (remaining % 2 == 1L) result = reduce(result, current)
            current = reduce(current, current)
            remaining /= 2
        }
        return result
    }

    private fun plusMod(a: Long, b: Long, m: Long): Long {
        if (0L == b) return a
        val mb = m - b
        return if (a >= mb) a - mb
        else m - mb + a
    }

    internal fun timesMod(a: Long, b: Long, mod: Long): Long = try {
        Math.multiplyExact(a % mod, b % mod).mod(mod)
    } catch (ex: ArithmeticException) {
        reduceByDoubling(0, a.mod(mod), b.mod(mod)) { acc, it -> plusMod(acc, it, mod) }
    }

    internal fun divMod(a: Long, b: Long, mod: Long): Long {
        // find the multiplicative modular inverse of b and multiply by a (i.e. calculate a * b^-1 mod m)
        // https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm#Computing_multiplicative_inverses_in_modular_structures
        fun inverse(a: Long): Long {
            var t = 0L
            var nt = 1L
            var r = mod
            var nr = a

            while (nr != 0L) {
                val q = r / nr
                (t - q * nt).let { t = nt; nt = it }
                (r - q * nr).let { r = nr; nr = it }
            }

            require(r <= 1) { "$a is not invertible under $mod" }
            return if (t >= 0) t else t + mod
        }

        return timesMod(a, inverse(b), mod)
    }

    data class Technique(private val a: Long, private val b: Long, internal val cards: Long) {
        fun positionOf(card: Long): Long = plusMod(timesMod(a, card, cards), b, cards)

        fun cardAt(pos: Long): Long = divMod(pos - b, a, cards)

        operator fun times(n: Long) = reduceByDoubling(Technique(1, 0, cards), this, n) { a, b -> a compose b }

        infix fun compose(other: Technique): Technique {
            require(cards == other.cards) { "cannot combine techniques for different deck sizes" }
            return Technique(
                a = timesMod(other.a, a, cards),
                b = plusMod(timesMod(other.a, b, cards), other.b, cards),
                cards = cards
            )
        }

        companion object {
            fun stack(cards: Long) = Technique((-1).mod(cards), (-1).mod(cards), cards)
            fun cut(cut: Long, cards: Long) = Technique(1, (-cut).mod(cards), cards)
            fun deal(increment: Long, cards: Long) = Technique(increment, 0, cards)
        }
    }
}
