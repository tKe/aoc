package year2023

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(
    19,
//    input = InputProvider.Example
//    warmup = aok.Warmup.iterations(10_000), runs = 300,
)

@AoKSolution
object Day19 : PuzDSL({

    val parser = parser {
        val (rules, items) = input.split("\n\n")
        rules.lines().associate { line ->
            val (name, conds) = line.split('{', '}')
            name to conds.split(',').map {
                if (':' !in it) return@map Fixed(Outcome.of(it))
                val field = Field.valueOf(it.take(1))
                val outcome = Outcome.of(it.substringAfter(':'))
                val value = it.substringBefore(':').drop(2).toInt()
                when (it[1]) {
                    '>' -> GreaterThan(field, value, outcome)
                    '<' -> LessThan(field, value, outcome)
                    else -> error("invalid operator '${it[1]}': $line")
                }
            }
        } to items.lines().map {
            val (x, m, a, s) = it.split('=', ',', '}').mapNotNull(String::toIntOrNull)
            Item(x, m, a, s)
        }
    }

    tailrec operator fun Map<String, List<Rule>>.invoke(item: Item, workflow: String = "in"): Boolean =
        when (val res = getValue(workflow).firstNotNullOf { it(item) }) {
            Reject -> false
            Accept -> true
            is Call -> invoke(item, res.workflow)
        }

    part1(parser) { (rules, items) ->
        items.filter(rules::invoke)
            .sumOf { (x, m, a, s) -> x + m + a + s }
    }

    fun IntRange.count() = if (isEmpty()) 0 else last - first + 1 //  why no stdlib?

    part2(parser) { (rules, _) ->
        DeepRecursiveFunction<Pair<MutableList<IntRange>, String>, Long> { (ranges, workflow) ->
            suspend fun DeepRecursiveScope<Pair<MutableList<IntRange>, String>, Long>.handleOutcome(
                outcome: Outcome,
                ranges: List<IntRange>,
            ) = when (outcome) {
                Reject -> 0
                Accept -> ranges.fold(1L) { a, r -> a * r.count() }
                is Call -> callRecursive(ranges.toMutableList() to outcome.workflow)
            }

            fun splitRange(field: Field, split: (IntRange) -> Pair<IntRange, IntRange>) =
                split(ranges[field.ordinal]).let { (take, rem) ->
                    ranges.toMutableList().also {
                        ranges[field.ordinal] = rem
                        it[field.ordinal] = take
                    }
                }

            rules.getValue(workflow).sumOf { rule ->
                when (rule) {
                    is Fixed -> handleOutcome(rule.outcome, ranges)
                    is GreaterThan -> handleOutcome(
                        rule.outcome,
                        splitRange(rule.field) { rule.value + 1..it.last to it.first..rule.value })

                    is LessThan -> handleOutcome(
                        rule.outcome,
                        splitRange(rule.field) { it.first..<rule.value to rule.value..it.last })
                }
            }
        }(List(4) { 1..4000 }.toMutableList() to "in")
    }
}) {
    data class Item(val x: Int, val m: Int, val a: Int, val s: Int)
    enum class Field(val get: (Item) -> Int) { x(Item::x), m(Item::m), a(Item::a), s(Item::s) }
    sealed interface Outcome {
        companion object {
            fun of(outcome: String) = when (outcome) {
                "R" -> Reject
                "A" -> Accept
                else -> Call(outcome)
            }
        }
    }

    data object Accept : Outcome
    data object Reject : Outcome
    data class Call(val workflow: String) : Outcome
    sealed interface Rule {
        operator fun invoke(item: Item): Outcome?
    }

    data class GreaterThan(val field: Field, val value: Int, val outcome: Outcome) : Rule {
        override fun invoke(item: Item) = outcome.takeIf { field.get(item) > value }
    }

    data class LessThan(val field: Field, val value: Int, val outcome: Outcome) : Rule {
        override fun invoke(item: Item) = outcome.takeIf { field.get(item) < value }
    }

    data class Fixed(val outcome: Outcome) : Rule {
        override fun invoke(item: Item) = outcome
    }
}
