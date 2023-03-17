package year2015

import aok.PuzDSL
import aoksp.AoKSolution


@AoKSolution
object Day15 : PuzDSL({
    data class Ingredient(
        val capacity: Int,
        val durability: Int,
        val flavour: Int,
        val texture: Int,
        val calories: Int,
    )

    operator fun Ingredient.times(other: Int) = Ingredient(
        other * capacity,
        other * durability,
        other * flavour,
        other * texture,
        other * calories
    )

    operator fun Ingredient.plus(other: Ingredient) = Ingredient(
        capacity + other.capacity,
        durability + other.durability,
        flavour + other.flavour,
        texture + other.texture,
        calories + other.calories,
    )

    fun Ingredient.score() = listOf(capacity, durability, flavour, texture).fold(1) { acc, i -> acc * maxOf(0, i) }

    fun String.parseIngredient() = split(',', ' ')
        .mapNotNull(String::toIntOrNull)
        .let { (a, b, c, d, e) -> Ingredient(a, b, c, d, e) }

    fun List<Ingredient>.scoreRecipe(predicate: (Ingredient) -> Boolean = { true }) = DeepRecursiveFunction<List<Int>, Int> { quantities ->
        val remaining = 100 - quantities.sum()
        when (quantities.size) {
            lastIndex -> zip(quantities + remaining, Ingredient::times)
                .reduce(Ingredient::plus)
                .takeIf(predicate)?.score() ?: 0

            else -> (0..remaining).maxOf { callRecursive(quantities + it) }
        }
    }(emptyList())

    part1 {
        lines.map(String::parseIngredient)
            .scoreRecipe()
    }
    part2 {
        lines.map(String::parseIngredient)
            .scoreRecipe { it.calories == 500 }
    }
})

fun main() = solveDay(
    15,
//    input = InputProvider.raw(
//        """
//            Butterscotch: capacity -1, durability -2, flavor 6, texture 3, calories 8
//            Cinnamon: capacity 2, durability 3, flavor -2, texture -1, calories 3
//        """.trimIndent()
//    )
)
