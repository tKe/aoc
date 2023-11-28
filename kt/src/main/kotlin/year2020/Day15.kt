package year2020

import aok.PuzDSL
import aoksp.AoKSolution
import utils.splitIntsNotNull

fun main() = solveDay(15)

@AoKSolution
object Day15 : PuzDSL({
    val parse = parser { input.splitIntsNotNull(",") }

    fun List<Int>.play(n: Int): Int {
        val lastSaid = IntArray(n) { -1 }
        fun IntArray.put(k: Int, v: Int) = get(k).also { set(k, v) }.takeIf { it >= 0 }
        forEachIndexed { idx, i -> lastSaid[i] = idx }

        var delta = 0
        for (idx in size..<n - 1) delta = idx - (lastSaid.put(delta, idx) ?: idx)
        return delta
    }

    part1(parse) { it.play(2020) }
    part2(parse) { it.play(30_000_000) }
})