package year2022

import aok.PuzzleInput
import aoksp.AoKSolution
import solveAll

fun main(): Unit = solveAll(warmupIterations = 200) { year == 2022 && day == 11 }

@AoKSolution
object Day11 {
    private data class Monkey(
        val operation: WorryOperation,
        val test: Int,
        val monkeyTrue: Int,
        val monkeyFalse: Int,
    )

    context(PuzzleInput)
    private fun parse() = lines.drop(1).chunked(7) { (items, op, test, ifTrue, ifFalse) ->
        val monkeyItems = items.substringAfter(": ").split(", ").map(String::toInt)
        monkeyItems to Monkey(
            operation = op.toWorryOperation(),
            test = test.substringAfter("divisible by ").toInt(),
            monkeyTrue = ifTrue.substringAfter("throw to monkey ").toInt(),
            monkeyFalse = ifFalse.substringAfter("throw to monkey ").toInt()
        )
    }.unzip()

    private fun String.toWorryOperation(): WorryOperation = when {
        contains("old * old") -> ({ it * it })
        contains("old + ") -> substringAfter("old + ").toInt()::plus
        contains("old * ") -> substringAfter("old * ").toInt()::times
        else -> error("Unhandled operation: $this")
    }

    private fun List<Monkey>.conductBusiness(
        initialItems: List<List<Int>>,
        rounds: Int,
        mitigate: WorryOperation
    ): Long {
        fun Monkey.nextMonkey(worry: Worry) = if (worry % test == 0L) monkeyTrue else monkeyFalse

        val counts = IntArray(size)
        val mitigatedMonkeys = map { monkey ->
            monkey.copy(operation = { monkey.operation(it).let(mitigate) })
        }
        val items = initialItems.map { it.map(Int::toLong).toMutableList() }

        repeat(rounds) {
            for ((idx, monkey) in mitigatedMonkeys.withIndex()) {
                val monkeyItems = with(items[idx]) { toList().also { clear() } }
                monkeyItems.forEach { item ->
                    val new = monkey.operation(item)
                    items[monkey.nextMonkey(new)] += new
                }
                counts[idx] += monkeyItems.size
            }
        }

        return counts.sortedDescending().let { (a, b) -> a.toLong() * b }
    }

    context(PuzzleInput)
    fun part1(): Long {
        val (items, monkeys) = parse()
        return monkeys.conductBusiness(items, rounds = 20) { it / 3 }
    }

    context(PuzzleInput)
    fun part2(): Long {
        val (items, monkeys) = parse()
        val factor = monkeys.map(Monkey::test).reduce(Int::times)
        return monkeys.conductBusiness(items, rounds = 10_000) { it % factor }
    }
}

typealias WorryOperation = (Worry) -> Worry
typealias Worry = Long
