package year2024

import aok.PuzDSL
import aok.PuzzleInput
import aok.solveAll
import aok.warmup
import aoksp.AoKSolution
import utils.splitInts
import utils.splitIntsNotNull
import java.util.*
import kotlin.Comparator
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day05 : PuzDSL({
    val ruleParser = parser {
        fun List<Pair<Int, Int>>.toComparator() = Comparator<Int> { a, b ->
            for ((first, second) in this) {
                return@Comparator when {
                    first == a && second == b -> -1
                    first == b && second == a -> 1
                    else -> continue
                }
            }
            0
        }

        val (rules, updates) = input.split("\n\n", limit = 2)
        val comparator = rules.lines()
            .map { it.splitIntsNotNull("|").let { (a, b) -> a to b } }
            .toComparator()
        val parsedUpdates = updates.lines().map { it.splitIntsNotNull(",") }
        parsedUpdates to comparator
    }

    part1(ruleParser) { (updates, rules) ->
        updates.filter { it == it.sortedWith(rules) }
            .sumOf { it[it.size / 2] }
    }

    part2(ruleParser) { (updates, rules) ->
        updates.mapNotNull { update -> update.sortedWith(rules).takeIf { it != update } }
            .sumOf { it[it.size / 2] }
    }
})

@AoKSolution
object Day05Map : PuzDSL({
    val ruleParser = parser {
        fun Map<Int, List<Int>>.toComparator() = Comparator<Int> { a, b ->
            get(a)?.let { if (b in it) -1 else 1 }
                ?: get(b)?.let { if (a in it) 1 else -1 }
                ?: 0
        }

        val (rules, updates) = input.split("\n\n", limit = 2).map { it.lines() }
        val comparator = rules.map { it.splitInts('|') }
            .groupBy({ (a) -> a }, { (_, b) -> b })
            .toComparator()
        val parsedUpdates = updates.map { it.splitInts(',') }
        parsedUpdates to comparator
    }

    fun <T> Comparator<T>.isSorted(list: List<T>): Boolean {
        list.zipWithNext { a, b -> if (compare(a, b) > 0) return false }
        return true
    }

    part1(ruleParser) { (updates, rules) ->
        updates.filter(rules::isSorted).sumOf { it[it.size / 2] }
    }

    part2(ruleParser) { (updates, rules) ->
        updates.sumOf {
            val sorted = it.sortedWith(rules)
            if (sorted != it) sorted[it.size / 2] else 0
        }
    }
})

@AoKSolution
object Day05BitSet : PuzDSL({
    val ruleParser = parser {
        val (rules, updates) = input.split("\n\n", limit = 2).map { it.lines() }

        infix fun Int.merge(other: Int): Int = or(other shl 7)
        fun BitSet.asComparator(): Comparator<Int> = Comparator { o1, o2 ->
            when {
                get(o1 merge o2) -> -1
                get(o2 merge o1) -> 1
                else -> 0
            }
        }

        val comparator = BitSet().apply {
            for (rule in rules) {
                rule.splitInts('|')
                    .also { (a, b) -> set(a merge b) }
            }
        }.asComparator()

        val parsedUpdates = updates.map { it.splitInts(',') }
        parsedUpdates to comparator
    }

    fun <T> Comparator<T>.isSorted(list: List<T>): Boolean {
        list.zipWithNext { a, b -> if (compare(a, b) > 0) return false }
        return true
    }

    part1(ruleParser) { (updates, rules) ->
        updates.filter(rules::isSorted).sumOf { it[it.size / 2] }
    }

    part2(ruleParser) { (updates, rules) ->
        updates.sumOf {
            val sorted = it.sortedWith(rules)
            if (sorted != it) sorted[it.size / 2] else 0
        }
    }
})

fun main() = queryDay(5).warmup().solveAll(5)//, warmup = eachFor(5.seconds), runs = 500)