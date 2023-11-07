package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import arrow.core.andThen

fun main() = solveDay(
    14,
//    input = aok.InputProvider.raw(
//        """
//171 ORE => 8 CNZTR
//7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL
//114 ORE => 4 BHXH
//14 VRPVC => 6 BMBT
//6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL
//6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT
//15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW
//13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW
//5 BMBT => 4 WPTQ
//189 ORE => 9 KTJDG
//1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP
//12 VRPVC, 27 CNZTR => 2 XDBXC
//15 KTJDG, 12 BHXH => 5 XCVML
//3 BHXH, 2 VRPVC => 7 MZWV
//121 ORE => 7 VRPVC
//7 XCVML => 6 RJRHP
//5 BHXH, 4 VRPVC => 5 LTCX
//        """.trimIndent()
//    )
)

@AoKSolution
object Day14 : PuzDSL({
    data class Recipe(val compound: String, val qty: Int, val ingredients: Map<String, Int>)

    val parseRecipes = lineParser {
        val (ing, make) = it.split(" => ")
        val (qty, makes) = make.split(" ")
        val ingredients = ing.split(", ").associate {
            val (q, ingredient) = it.split(" ")
            ingredient to q.toInt()
        }
        Recipe(makes, qty.toInt(), ingredients)
    }.andThen { it.associateBy(Recipe::compound) }

    fun Map<String, Recipe>.oreRequired(requiredFuel: Long = 1L): Long {
        val required = mutableMapOf("FUEL" to requiredFuel)
        while (true) {
            val (compound, requiredQty) = required.entries.firstOrNull { (k, v) -> k != "ORE" && v > 0 } ?: break
            val recipe = getValue(compound)
            val batches = requiredQty / recipe.qty + if (requiredQty % recipe.qty > 0) 1 else 0
            required.merge(compound, -batches * recipe.qty, Long::plus)
            for ((ingredient, qty) in recipe.ingredients) required.merge(ingredient, qty * batches, Long::plus)
        }
        return required["ORE"] ?: 0
    }

    part1(parseRecipes) { recipes ->
        recipes.oreRequired()
    }

    part2(parseRecipes) { recipes ->
        val maxOre = 1_000_000_000_000
        val min = maxOre / recipes.oreRequired(1)

        var range = min.. 2 * min
        while (range.last - range.first > 1) {
            val mid = (range.first + range.last) / 2
            range = when {
                recipes.oreRequired(mid) > maxOre -> range.first..mid
                else -> mid..range.last
            }
        }
        range.first
    }
})

