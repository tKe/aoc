package year2019

import aok.Parser
import aok.PuzDSL
import aoksp.AoKSolution
import arrow.core.andThen
import year2019.Day24.Counter.Companion.count
import year2019.Day24.Counter.Companion.incIf

fun main() = solveDay(
    24,
)

@AoKSolution
object Day24 : PuzDSL({
    fun <T> Sequence<T>.firstDuplicate() = first(mutableSetOf<T>()::add.andThen { !it })

    part1(Eris) { eris ->
        generateSequence(eris, Eris::evolve).firstDuplicate().biodiversity
    }

    part2(UberEris) { uberEris ->
        generateSequence(uberEris, UberEris::evolve).elementAt(200).bugs
    }
}) {
    @JvmInline
    value class Eris(private val state: Int) {
        val isEmpty get() = state == 0
        val biodiversity get() = state // happy coincidence
        val bugs get() = state.countOneBits()
        operator fun get(x: Int, y: Int) =
            x in 0..<5 && y in 0..<5 && state.shr(5 * y + x).and(1) == 1

        private fun neighbours(x: Int, y: Int) = count {
            incIf(this[x - 1, y], this[x + 1, y], this[x, y - 1], this[x, y + 1])
        }

        fun evolve(neighbours: (x: Int, y: Int) -> Int = this::neighbours): Eris {
            var i = state
            for (y in 0..<5) for (x in 0..<5) {
                val n = neighbours(x, y)
                val bit = 1.shl(y * 5 + x)
                if (this[x, y] && n != 1) i = i and bit.inv()
                else if (n == 1 || n == 2) i = i.or(bit)
            }
            return Eris(i)
        }

        companion object : Parser<Eris> by (Parser {
            Eris(input.reversed().fold(0) { acc, c ->
                when (c) {
                    '#' -> acc.shl(1).or(1)
                    '.' -> acc.shl(1)
                    else -> acc
                }
            })
        })
    }

    @JvmInline
    value class UberEris(private val erisii: List<Eris>) {
        companion object : Parser<UberEris> by Eris.map({ UberEris(listOf(it)) })

        val bugs get() = erisii.sumOf(Eris::bugs)

        private fun get(z: Int) = erisii.getOrNull(z)
        private operator fun get(x: Int, y: Int, z: Int) = get(z)?.get(x, y) ?: false

        private fun neighbours(x: Int, y: Int, z: Int) = count {
            if (x == 2 && y == 2) return@count // will always die, will never infect
            val current = erisii.getOrNull(z)
            val outer = erisii.getOrNull(z - 1)
            val inner = erisii.getOrNull(z + 1)
            operator fun Eris?.get(x: Int, y: Int) = this?.get(x, y) ?: false

            // ups
            when {
                y == 0 -> incIf(outer[2, 1])
                y == 3 && x == 2 -> repeat(5) { incIf(inner[it, 4]) }
                else -> incIf(current[x, y - 1])
            }

            // downs
            when {
                y == 4 -> incIf(outer[2, 3])
                y == 1 && x == 2 -> repeat(5) { incIf(inner[it, 0]) }
                else -> incIf(current[x, y + 1])
            }

            // lefts
            when {
                x == 0 -> incIf(outer[1, 2])
                x == 3 && y == 2 -> repeat(5) { incIf(inner[4, it]) }
                else -> incIf(current[x - 1, y])
            }

            // rights
            when {
                x == 4 -> incIf(outer[3, 2])
                x == 1 && y == 2 -> repeat(5) { incIf(inner[0, it]) }
                else -> incIf(current[x + 1, y])
            }
        }

        fun evolve(): UberEris = UberEris(buildList(erisii.size + 2) {
            fun eris(z: Int) = erisii.getOrElse(z) { Eris(0) }.evolve { x, y -> neighbours(x, y, z) }
            if (!erisii.first().isEmpty) add(eris(-1))
            repeat(erisii.size) { add(eris(it)) }
            if (!erisii.last().isEmpty) add(eris(erisii.size))
        })
    }

    @JvmInline
    value class Counter(private val count: IntArray = IntArray(1)) {
        val result get() = count[0]
        fun inc(by: Int) {
            count[0] += by
        }

        fun inc() = inc(1)


        companion object {
            inline fun count(block: context(Counter) () -> Unit) = Counter().apply(block).result

            context(counter: Counter)
            fun incIf(vararg condition: Boolean, by: Int = 1) =
                counter.inc(condition.count { it } * by)
        }
    }


}
