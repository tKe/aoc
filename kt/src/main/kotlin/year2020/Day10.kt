package year2020

import aok.PuzDSL
import aoksp.AoKSolution
import utils.Parsers

fun main() = solveDay(
    10,
)

@AoKSolution
object Day10 : PuzDSL({
    fun List<Int>.deltas(): List<Int> = sorted()
        .let { listOf(0) + it + (it.last() + 3) }
        .zipWithNext { a, b -> b - a }

    part1(Parsers.Ints) { adapters ->
        adapters.deltas().groupingBy { it }.eachCount()
            .values.reduce(Int::times)
    }

    fun String.cutOn(vararg separators: String) = split(*separators, limit = 2).let {
        when (it.size) {
            0 -> "" to null
            1 -> it.first() to null
            else -> it.first() to it.last()
        }
    }

    part2(Parsers.Ints) { adapters ->
        // treat the input only as the deltas between the (ordered) string of adapters
        val deltaSeq = adapters.deltas().joinToString("") // only chars 1-3 are expected

        // split the string into sections based on "pivots" where we cannot rearrange any other way (3 and 2-2)
        // we can multiply the number of options for left and right of a pivot to get the total for the full string
        // otherwise if we didn't have a pivot, sum the options if we skip a delta and if we combine a delta
        DeepRecursiveFunction<String, Long> { seq ->
            val (start, end) = seq.cutOn("3", "22")
            when {
                end != null -> callRecursive(start) * callRecursive(end)
                else -> when (seq.length) {
                    0, 1 -> 1
                    2 -> 2
                    else -> {
                        val remaining = seq.drop(2)
                        val (a, b) = seq.take(2).map(Char::digitToInt)
                        require((a + b) in 1..3) { "failed! got ${a + b} from $seq" }
                        callRecursive("$a$remaining") + callRecursive("${a + b}$remaining")
                    }
                }
            }
        }(deltaSeq)
    }
})

