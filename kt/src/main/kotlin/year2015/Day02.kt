package year2015

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day02 : PuzDSL({
    part1 {
        lineSeq
            .map { it.split("x").map(String::toInt).sorted() }
            .map { (l, w, h) -> listOf(l * w, w * h, h * l) }
            .sumOf { it.first() + it.sumOf(2::times) }
    }

    part2 {
        lineSeq
            .map { it.split("x").map(String::toInt).sorted() }
            .sumOf { it.take(2).sumOf(2::times) + it.reduce(Int::times) }
    }
})

fun main() = solveDay(2)
