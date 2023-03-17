package year2015

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution


@AoKSolution
object Day13 : PuzDSL({
    part1 { optimalHappiness() }
    part2 { optimalHappiness("Me") }
})

private fun PuzzleInput.optimalHappiness(vararg alreadySeated: String): Int {
    val costs = lines
        .map("""([^ ]+) would (gain|lose) (\d+) happiness units by sitting next to ([^.]+)\.""".toRegex()) { (person, impact, amount, neighbour) ->
            val delta = when (impact) {
                "lose" -> -1
                else -> 1
            } * amount.toInt()
            Triple(person, neighbour, delta)
        }
        .groupBy { it.first }
        .mapValues { it.value.associate { (_, neighbour, amount) -> neighbour to amount } }
    fun cost(a: String, b: String) = costs[a]?.get(b) ?: 0

    return DeepRecursiveFunction<Pair<List<String>, Set<String>>, Int> { (seated, remaining) ->
        remaining.maxOfOrNull { callRecursive(seated + it to remaining - it) }
            ?: (seated + seated.first()).zipWithNext { a, b -> cost(a, b) + cost(b, a) }.sum()
    }(alreadySeated.toList() to costs.keys)
}

private fun <R> List<String>.map(pattern: Regex, transform: (MatchResult.Destructured) -> R) =
    mapNotNull(pattern::matchEntire).map(MatchResult::destructured).map(transform)

fun main() = solveDay(
    13,
//    input = InputProvider.raw(
//        """
//            Alice would gain 54 happiness units by sitting next to Bob.
//            Alice would lose 79 happiness units by sitting next to Carol.
//            Alice would lose 2 happiness units by sitting next to David.
//            Bob would gain 83 happiness units by sitting next to Alice.
//            Bob would lose 7 happiness units by sitting next to Carol.
//            Bob would lose 63 happiness units by sitting next to David.
//            Carol would lose 62 happiness units by sitting next to Alice.
//            Carol would gain 60 happiness units by sitting next to Bob.
//            Carol would gain 55 happiness units by sitting next to David.
//            David would gain 46 happiness units by sitting next to Alice.
//            David would lose 7 happiness units by sitting next to Bob.
//            David would gain 41 happiness units by sitting next to Carol.
//        """.trimIndent()
//    )
)
