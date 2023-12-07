package year2023

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(7)

@AoKSolution
object Day07 : PuzDSL({
    fun parse(ctor: (String) -> Hand) = lineParser {
        val hand = ctor(it.substringBefore(' '))
        val bid = it.substringAfter(' ').toInt()
        hand to bid
    }

    fun List<Pair<Hand, Int>>.winnings() = sortedWith(compareBy(compareBy(Hand::type, Hand::value)) { it.first })
        .mapIndexed { index, (_, bid) -> (index + 1) * bid }
        .sum()

    part1(parse(::Hand)) { it.winnings() }
    part2(parse(Hand::withJokers)) { it.winnings() }
}) {
    data class Hand(val value: Int, val type: Type) {
        constructor(hand: String) : this(hand.toValue(), hand.toHandType())

        companion object {
            fun withJokers(hand: String) = Hand(hand.replace('J', '*'))

            private fun String.toHandType() = Type.fromCounts(
                groupingBy { it }.eachCountTo(mutableMapOf()).apply {
                    remove('*')?.let { wildcards ->
                        val strongest = maxWithOrNull(compareBy({ it.value }, { indexOf(it.key) }))?.key ?: 'A'
                        merge(strongest, wildcards, Int::plus)
                    }
                }.values
            )

            private const val CARD_ORDER = "*23456789TJQKA"
            private fun String.toValue(): Int =
                fold(0) { acc, c -> acc * CARD_ORDER.length + CARD_ORDER.indexOf(c) }
        }

        enum class Type {
            High,
            OnePair,
            TwoPair,
            ThreeOfAKind,
            FullHouse,
            FourOfAKind,
            FiveOfAKind;

            companion object {
                fun fromCounts(counts: Collection<Int>) = when (counts.size) {
                    1 -> FiveOfAKind
                    2 -> if (counts.max() == 4) FourOfAKind else FullHouse
                    3 -> if (counts.max() == 3) ThreeOfAKind else TwoPair
                    4 -> OnePair
                    else -> High
                }
            }
        }
    }
}
