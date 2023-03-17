package year2017

import aok.PuzDSL
import aok.Warmup
import aoksp.AoKSolution

@AoKSolution
object Day07 : PuzDSL({
    val parseTowers = lineParser {
        it.split(" (", ") -> ", ", ")
    }

    part1 {
        buildSet {
            val stackedTowers = mutableSetOf<String>()
            for (it in parseTowers()) {
                stackedTowers += it.drop(2)
                add(it.first())
            }
            removeAll(stackedTowers)
        }.single()
    }

    part2 {
        val splits = parseTowers()
        val weight = splits.associate { (name, weight) -> name to weight.trimEnd(')').toInt() }
        val stacks = splits.associate { it.first() to it.drop(2) }
        val stackWeights = buildMap {
            fun getOrCalculate(tower: String): Int =
                getOrPut(tower) { weight.getValue(tower) + stacks.getValue(tower).sumOf(::getOrCalculate) }
            stacks.keys.forEach(::getOrCalculate)
        }
        stacks.values
            .map { it.groupBy(stackWeights::getValue) }
            .filter { it.size == 2 }
            .minBy { it.keys.min() }
            .let { subTowers ->
                val (wrongSize, tower) = subTowers.entries.single { it.value.size == 1 }
                val rightSize = (subTowers.keys - wrongSize).single()
                weight.getValue(tower.single()) - wrongSize + rightSize
            }
    }
})

fun main(): Unit = solveDay(7, warmup = Warmup.iterations(800), runs = 3)
