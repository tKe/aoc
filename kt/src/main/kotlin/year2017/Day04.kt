package year2017

import aok.PuzDSL
import aok.Warmup
import aoksp.AoKSolution

@AoKSolution
object Day04 : PuzDSL({
    fun <T> Sequence<T>.isUnique() = all(mutableSetOf<T>()::add)
    fun String.sorted() = toCharArray().apply { sort() }.concatToString()
    part1 { lines.count { it.splitToSequence(' ').isUnique() } }
    part2 { lines.count { it.splitToSequence(' ').map(String::sorted).isUnique() } }
})

fun main(): Unit = solveDay(4, warmup = Warmup.iterations(1000))
