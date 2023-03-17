package year2017

import aok.InputProvider
import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day05 : PuzDSL({
    part1 {
        lines.map(String::toInt).toIntArray().run {
            generateSequence(0) {
                (it + this[it]++).takeIf(indices::contains)
            }.count()
        }
    }
    part2 {
        lines.map(String::toInt).toIntArray().run {
            generateSequence(0) {
                val ofs = get(it)
                set(it, ofs + if (ofs >= 3) -1 else 1)
                (it + ofs).takeIf(indices::contains)
            }.count()
        }
    }
})

fun main(): Unit = solveDay(5)
