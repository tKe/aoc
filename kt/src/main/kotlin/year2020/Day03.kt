package year2020

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(
    3,
//    input = aok.InputProvider.Example
)

@AoKSolution
object Day03 : PuzDSL({

    val parser = lineParser { it.map('#'::equals) }

    fun List<List<Boolean>>.countTrees(x: Int, y: Int) = indices.step(y).withIndex()
        .count { (idx, y) ->
            get(y).let { r -> r[idx * x % r.size] }
        }

    part1(parser) { geology -> geology.countTrees(3, 1) }
    part2(parser) { geology ->
        geology.countTrees(1, 1).toLong() *
                geology.countTrees(3, 1) *
                geology.countTrees(5, 1) *
                geology.countTrees(7, 1) *
                geology.countTrees(1, 2)
    }
})

