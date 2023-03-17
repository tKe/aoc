package year2015

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

@AoKSolution
object Day16 : PuzDSL({
    fun String.parseCompound() = split(": ")
        .let { (compound, amound) -> compound to amound.toInt() }

    fun PuzzleInput.sues() = lines.associate {
        val (sue, samples) = it.split(": ", limit = 2)
        sue to samples.split(", ").associate(String::parseCompound)
    }

    val wrappingSamples = """
        children: 3
        cats: 7
        samoyeds: 2
        pomeranians: 3
        akitas: 0
        vizslas: 0
        goldfish: 5
        trees: 3
        cars: 2
        perfumes: 1
    """.trimIndent().lines().associate(String::parseCompound)

    part1 {
        sues()
            .filterValues { it.all(wrappingSamples.entries::contains) }
            .keys.single()
    }

    part2 {
        sues()
            .filterValues {
                it.all { (compound, amount) ->
                    when (compound) {
                        "cats", "trees" -> amount > wrappingSamples[compound]!!
                        "pomeranians", "goldfish" -> amount < wrappingSamples[compound]!!
                        else -> amount == wrappingSamples[compound]
                    }
                }
            }
            .keys.single()
    }
})

fun main() = solveDay(
    16,
)
