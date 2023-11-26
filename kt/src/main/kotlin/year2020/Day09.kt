package year2020

import aok.PuzDSL
import aoksp.AoKSolution
import utils.Parsers
import utils.combinations
import utils.splitWhen

fun main() = solveDay(
    9,
)

@AoKSolution
object Day09 : PuzDSL({
    fun List<Long>.invalidXMAS(preamble: Int = 25) = withIndex().drop(preamble)
        .first { (idx, value) ->
            subList(idx - preamble, idx).combinations(2).none { (a, b) -> a + b == value }
        }
        .value

    part1(Parsers.Longs) {
        it.invalidXMAS()
    }

    part2(Parsers.Longs) { stream ->
        val invalid = stream.invalidXMAS()

        fun summedRange(range: List<Long>): List<Long>? {
            var sum = 0L
            var start = 0
            repeat(range.size) {
                sum += range[it]
                while (sum > invalid) sum -= range[start++]
                if (sum == invalid) return range.slice(start..it)
            }
            return null
        }

        stream.splitWhen { it >= invalid }
            .firstNotNullOf(::summedRange)
            .run { min() + max() }
    }
})

