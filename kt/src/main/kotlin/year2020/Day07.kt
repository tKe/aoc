package year2020

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(
    7,
//    input = aok.InputProvider.raw("""
//        shiny gold bags contain 2 dark red bags.
//        dark red bags contain 2 dark orange bags.
//        dark orange bags contain 2 dark yellow bags.
//        dark yellow bags contain 2 dark green bags.
//        dark green bags contain 2 dark blue bags.
//        dark blue bags contain 2 dark violet bags.
//        dark violet bags contain no other bags.
//    """.trimIndent())
)

@AoKSolution
object Day07 : PuzDSL({
    val bagMatcher = """(\d+) (\w+ \w+) bags?""".toRegex()
    val parser = lineParser { line ->
        val (bag, contents) = line.split(" bags contain ", limit = 2)
        bag to bagMatcher.findAll(contents).associate {
            val (count, subBag) = it.destructured
            subBag to count.toInt()
        }
    }.map {
        it.toMap()
    }

    fun <K1, K2> Map<K1, Map<K2, *>>.invert(): Map<K2, Set<K1>> =
        flatMap { (k1, m) -> m.keys.map { it to k1 } }
            .groupBy({ it.first }, { it.second })
            .mapValues { it.value.toSet() }

    part1(parser) { rules ->
        val canContain = rules.invert()
        buildSet {
            addAll(canContain["shiny gold"].orEmpty())
            do {
                val next = mapNotNull { canContain[it] }.flatten()
            } while (addAll(next))
        }.size
    }

    part2(parser) { rules ->
        val cache = mutableMapOf<String, Int>()
        fun countBags(colour: String): Int = cache.getOrPut(colour) {
            rules[colour].orEmpty().asSequence()
                .sumOf { (nested, count) -> count + count * countBags(nested) }
        }
        countBags("shiny gold")
    }
})

