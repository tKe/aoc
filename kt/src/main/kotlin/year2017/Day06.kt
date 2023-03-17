package year2017

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day06 : PuzDSL({
    fun IntArray.redistribute() = max().let { blocks ->
        var idx = indexOf(blocks)
        this[idx++] = 0
        repeat(blocks) { this[idx++ % size]++ }
    }

    part1 {
        val banks = lines.first().split('\t').map(String::toInt).toIntArray()
        generateSequence(banks.toList()) { banks.apply { redistribute() }.toList() }
            .takeWhile(mutableSetOf<Any>()::add)
            .count()
    }

    part2 {
        val banks = lines.first().split('\t').map(String::toInt).toIntArray()
        val seen = mutableMapOf<List<Int>, Int>()
        generateSequence(banks.toList()) { banks.apply { redistribute() }.toList() }
            .mapIndexed { index, it -> index - seen.getOrPut(it) { index } }
            .first { it > 0 }
    }
})

fun main(): Unit = solveDay(6)
