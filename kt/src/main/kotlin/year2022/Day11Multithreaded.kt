package year2022

import aok.PuzzleInput
import aok.Warmup
import aoksp.AoKSolution
import arrow.fx.coroutines.parMapUnordered
import kotlinx.coroutines.flow.asFlow

fun main() = solveDay(11, warmup = Warmup.iterations(200))

@AoKSolution
object Day11Multithreaded {
    context(PuzzleInput)
    suspend fun part1(): Long {
        val (items, monkeys) = parse()
        return monkeys.conductBusiness(items, 20) { it / 3 }
    }

    context(PuzzleInput)
    suspend fun part2(): Long {
        val (items, monkeys) = parse()
        val factor = monkeys.fold(1) { f, m -> f * m.test }
        return monkeys.conductBusiness(items, 10_000) { it % factor }
    }

    private data class Monkey(
        val handle: WorryOperation,
        val test: Int,
        val monkeyTrue: Int,
        val monkeyFalse: Int,
    )

    context(PuzzleInput)
    private fun parse() = lines.drop(1).chunked(7) { (items, op, test, ifTrue, ifFalse) ->
        val monkeyItems = items.substringAfter(": ")
            .split(", ").map(String::toInt)

        monkeyItems to Monkey(
            handle = when {
                "old * old" in op -> ({ it * it })
                "old + " in op -> op.substringAfter("old + ").toInt()::plus
                "old * " in op -> op.substringAfter("old * ").toInt()::times
                else -> error("Unhandled operation: $op")
            },
            test = test.substringAfter("divisible by ").toInt(),
            monkeyTrue = ifTrue.substringAfter("throw to monkey ").toInt(),
            monkeyFalse = ifFalse.substringAfter("throw to monkey ").toInt()
        )
    }.unzip()

    private fun Monkey.nextMonkey(item: Long) =
        if (item % test == 0L) monkeyTrue else monkeyFalse

    private inline fun List<Monkey>.processItem(
        item: Int,
        startMonkey: Int = 0,
        rounds: Int = 20,
        crossinline mitigate: WorryOperation = { it / 3 }
    ) = IntArray(size).also { counts ->
        var round = 0
        var worry = item.toLong()
        var monkey = startMonkey
        while (round < rounds) {
            counts[monkey]++
            with(get(monkey)) {
                worry = handle(worry).let(mitigate)
                val nextMonkey = nextMonkey(worry)
                if (nextMonkey < monkey) round++
                monkey = nextMonkey
            }
        }
    }

    private suspend inline fun List<Monkey>.conductBusiness(
        items: List<List<Int>>,
        rounds: Int,
        crossinline mitigation: WorryOperation
    ): Long {
        val totals = IntArray(size)

        items
            .flatMapIndexed { startMonkey, values ->
                values.map(startMonkey::to)
            }.asFlow()
            .parMapUnordered { (startMonkey, item) ->
                processItem(item, startMonkey, rounds, mitigation)
            }
            .collect {
                it.forEachIndexed { monkey, count ->
                    totals[monkey] += count
                }
            }

        return totals.sortedDescending().let { (a, b) -> a.toLong() * b }
    }
}
