package year2018

import aok.PuzDSL
import aoksp.AoKSolution

fun main(): Unit = solveDay(14, input = aok.InputProvider.raw("165061"))

@AoKSolution
object Day14 : PuzDSL({
    val scoreboard = sequence {
        val recipes = mutableListOf(3, 7)
        yieldAll(recipes)
        var elf1 = 0
        var elf2 = 1
        while (true) {
            val recipe1 = recipes[elf1]
            val recipe2 = recipes[elf2]
            val sum = recipe1 + recipe2
            val newRecipes = if (sum >= 10) listOf(sum / 10, sum % 10) else listOf(sum)
            yieldAll(newRecipes)
            recipes += newRecipes
            elf1 = (elf1 + 1 + recipe1) % recipes.size
            elf2 = (elf2 + 1 + recipe2) % recipes.size
        }
    }

    part1 {
        scoreboard.drop(input.toInt()).take(10).joinToString("")
    }
    part2 {
        val needle = input.map(Char::digitToInt)
        scoreboard.windowed(needle.size, transform = needle::equals).indexOf(true)
    }
})
