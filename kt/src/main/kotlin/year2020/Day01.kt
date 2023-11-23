package year2020

import aok.PuzDSL
import aoksp.AoKSolution
import utils.Parsers
import utils.combinations

fun main() = solveDay(
    1,
//    input = aok.InputProvider.Example
)

@AoKSolution
object Day01 : PuzDSL({
    part1(Parsers.Ints) { costs ->
        costs.combinations(2).firstNotNullOf { (a, b) -> if (a + b == 2020) a * b else null }
    }
    part2(Parsers.Ints) { costs ->
        costs.combinations(3).firstNotNullOf { (a, b, c) -> if (a + b + c == 2020) a * b * c else null }
    }
})