package year2015

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

@AoKSolution
object Day09 : PuzDSL({
    fun <T : Comparable<T>> orderedPair(a: T, b: T) = if (a < b) a to b else b to a
    fun PuzzleInput.bestRoute(metric: List<Int>.() -> Int): Int {
        val distances = lines.associate {
            val (a, b, dist) = it.split(" to ", " = ")
            orderedPair(a, b) to dist.toInt()
        }
        val cities = distances.flatMap { it.key.toList() }.toSet()
        return DeepRecursiveFunction<List<String>, Int> { route ->
            (cities - route.toSet()).takeUnless { it.isEmpty() }
                ?.map { dest -> callRecursive(route + dest) }?.metric()
                ?: route.zipWithNext(::orderedPair).sumOf { distances.getValue(it) }
        }(emptyList())
    }

    part1 { bestRoute { min() } }
    part2 { bestRoute { max() } }
})

fun main() = solveDay(
    9,
//    input = InputProvider.raw(
//        """
//        London to Dublin = 464
//        London to Belfast = 518
//        Dublin to Belfast = 141
//    """.trimIndent()
//    )
)
