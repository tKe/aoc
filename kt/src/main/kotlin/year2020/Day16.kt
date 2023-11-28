package year2020

import aok.PuzDSL
import aoksp.AoKSolution
import utils.splitIntsNotNull

fun main() = solveDay(16)

@AoKSolution
object Day16 : PuzDSL({
    val parse = parser {
        val (sectRules, sectTicket, sectOthers) = input.split("\n\n")

        val rules = sectRules.lineSequence().associate {
            val (field, aStart, aEnd, bStart, bEnd) = it.split(": ", "-", " or ")
            field to (aStart.toInt()..aEnd.toInt() to bStart.toInt()..bEnd.toInt())
        }

        val ticket = sectTicket.split("\n").last().splitIntsNotNull(",")

        val others = sectOthers.lines().drop(1).map { it.splitIntsNotNull(",") }

        Triple(rules, ticket, others)
    }

    part1(parse) { (rules, _, others) ->
        others.flatten().filter {
            rules.values.none { (a, b) -> it in a || it in b }
        }.sum()
    }

    part2(parse) { (rules, ticket, others) ->
        val valid = others.filterNot {
            it.any { rules.values.none { (a, b) -> it in a || it in b } }
        }.plusElement(ticket)

        val pending = valid.flatMap(List<Int>::withIndex)
            .groupByTo(mutableMapOf(), { it.index }) { it.value }
        val mapped = mutableMapOf<String, Int>()
        while (pending.isNotEmpty()) {
            mapped += buildMap {
                for ((field, ranges) in (rules - mapped.keys))
                    for ((fieldIdx, values) in pending)
                        if (values.all { it in ranges.first || it in ranges.second })
                            merge(field, fieldIdx) { _, _ -> -1 }
                values.retainAll { it >= 0 }
                pending -= values
            }
        }

        mapped.mapValues { ticket[it.value] }
            .filterKeys { it.startsWith("departure") }
            .values.fold(1L, Long::times)
    }
})