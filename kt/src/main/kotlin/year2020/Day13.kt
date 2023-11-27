package year2020

import aok.PuzDSL
import aoksp.AoKSolution
import utils.splitIntsNotNull
import year2020.Day13.solve

fun main() = solveDay(
    13,
//    input = aok.InputProvider.Example
)

@AoKSolution
object Day13 : PuzDSL({
    part1 {
        val earliestDeparture = lines.first().toInt()
        val buses = lines.last().splitIntsNotNull(",")

        (0..buses.max()).firstNotNullOf { delay ->
            val depart = delay + earliestDeparture
            buses.firstNotNullOfOrNull { bus ->
                if (depart.mod(bus) == 0) delay * bus
                else null
            }
        }
    }

    part2 {
        lines.last().split(",").mapIndexedNotNull { idx, s ->
            // map each bus to a linear congruence where we still have `idx` steps to go before reaching 0
            // or ignore it if it's not a valid bus id (int)
            s.toIntOrNull()?.let { LinearCongruence(-idx, it) }
        }.solve()
    }
}) {
    // find the multiplicative modular inverse of b and multiply by a (i.e. calculate a * b^-1 mod m)
    // https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm#Computing_multiplicative_inverses_in_modular_structures
    private fun inverse(a: Long, mod: Long): Long {
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

    /**
     * representation of x = a mod n
     */
    data class LinearCongruence(val a: Long, val n: Long) {
        constructor(a: Int, n: Int) : this(a.toLong(), n.toLong())
    }

    fun List<LinearCongruence>.solve() =
        fold(1L) { acc, (_, n) -> acc * n }.let { m ->
            sumOf { (a, n) ->
                val ni = m / n
                (a * ni * inverse(ni, n)).mod(m)
            }.mod(m)
        }
}

