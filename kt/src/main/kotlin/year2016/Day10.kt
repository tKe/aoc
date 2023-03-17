package year2016

import aok.PuzDSL
import aok.PuzzleInput
import aok.Warmup
import aok.Warmup.Companion.eachFor
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day10 : PuzDSL({

    fun PuzzleInput.getBins(): Map<String, List<Int>> = buildMap<String, MutableList<Int>> {
        val bin = { it: String -> getOrPut(it, ::mutableListOf) }
        DeepRecursiveFunction { pending: List<String> ->
            pending.filter { instruction ->
                when {
                    instruction.startsWith("value ") -> {
                        val (value, bot) = instruction.split(" ").mapNotNull(String::toIntOrNull)
                        bin("bot $bot") += value
                    }

                    instruction.startsWith("bot ") -> {
                        val (source, low, high) = instruction.split(" gives low to ", " and high to ")
                        val values = bin(source)
                        if (values.size < 2) return@filter true
                        bin(low) += values.min()
                        bin(high) += values.max()
                    }
                }
                false
            }.let { if(it.isNotEmpty()) callRecursive(it) }
        }(lines)
    }

    part1 {
        getBins().entries
            .single { 61 in it.value && 17 in it.value }
    }

    part2 {
        listOf("output 0", "output 1", "output 2")
            .map(getBins()::getValue)
            .flatten()
            .fold(1, Int::times)
    }
})

fun main() = solveDay(
    10,
    warmup = eachFor(3.seconds)
//    input = InputProvider.raw("(25x3)(3x3)ABC(2x3)XY(5x2)PQRSTX(18x9)(3x2)TWO(5x7)SEVEN")
)
