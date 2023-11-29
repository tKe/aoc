package year2020

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(21)

@AoKSolution
object Day21 : PuzDSL({
    data class Food(val ingredients: Set<String>, val allergens: Set<String>)

    val parse = lineParser { line ->
        val (ingredients, allergens) = line.removeSuffix(")").split(" (contains ")
        Food(ingredients.splitToSequence(" ").toSet(), allergens.split(", ").toSet())
    }

    part1(parse) { food ->
        val allergenIngredients = buildMap<String, Set<String>> {
            for ((ingredients, allergens) in food) {
                for (allergen in allergens) {
                    merge(allergen, ingredients) { a, b -> a.intersect(b) }
                }
            }
        }

        val possibleAllergenIngredients = allergenIngredients.values.flatten().toSet()
        val nonAllergenIngredients = buildSet { food.flatMapTo(this) { it.ingredients - possibleAllergenIngredients } }
        nonAllergenIngredients.sumOf { food.count { (ing) -> it in ing } }
    }

    part2(parse) { food ->
        val allergenIngredientCandidates = buildMap<String, Set<String>> {
            for ((ingredients, allergens) in food) {
                for (allergen in allergens) {
                    merge(allergen, ingredients) { a, b -> a.intersect(b) }
                }
            }
        }

        val allergenIngredients = generateSequence(allergenIngredientCandidates) {
            val known = it.filterValues { it.size == 1 }
            val knownIngredients = known.values.flatten().toSet()
            known + (it - known.keys).mapValues { (_, v) -> v - knownIngredients }
        }.first { it.all { (_, v) -> v.size == 1 } }.mapValues { it.value.single() }

        allergenIngredients.toSortedMap().values.joinToString(",")
    }
})