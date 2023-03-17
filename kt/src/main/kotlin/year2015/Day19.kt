package year2015

import aok.InputProvider
import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution
import java.util.*

@AoKSolution
object Day19 : PuzDSL({
    fun PuzzleInput.parseInput() = input.split("\n\n")
        .let { (replacements, initial) ->
            replacements.lines()
                .map {
                    it.split(" => ").let { (a, b) -> a to b }
                } to initial.trim()
        }

    fun String.indexesOf(from: String) = generateSequence(indexOf(from)) {
        indexOf(from, startIndex = it + 1)
    }.takeWhile { it >= 0 }

    fun String.replaceAllOnce(from: String, to: String) =
        indexesOf(from).map { replaceRange(it, it + from.length, to) }

    part1 {
        val (replacements, state) = parseInput()

        replacements.flatMap { (from, to) ->
            state.indexesOf(from).map { state.replaceRange(it, it + from.length, to) }
        }.count(mutableSetOf<String>()::add)
    }

    part2 {
        val (replacements, molecule) = parseInput()

        DeepRecursiveFunction<String, Int?> { current ->
            replacements.firstNotNullOfOrNull { (from, to) ->
                current.replaceAllOnce(to, from).firstNotNullOfOrNull { next ->
                    when (next) {
                        "e" -> 1
                        else -> callRecursive(next)?.plus(1)
                    }
                }
            }
        }(molecule)
    }
})

fun main() = solveDay(
    19,
//    input = InputProvider.raw(
//        """
//        e => H
//        e => O
//        H => HO
//        H => OH
//        O => HH
//
//        HOH
//    """.trimIndent()
//    )
)
