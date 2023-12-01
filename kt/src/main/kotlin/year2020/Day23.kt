package year2020

import aok.PuzDSL
import aoksp.AoKSolution
import year2020.Day23.move
import year2020.Day23.toCups

fun main() = solveDay(23)

@AoKSolution
object Day23 : PuzDSL({
    val parse = parser {
        input.mapNotNull(Char::digitToIntOrNull)
    }

    part1(parse) { cups ->
        val (cup, one) = cups.toCups()
        cup.move(100)
        generateSequence(one.nextClockwise, Cup::nextClockwise)
            .map { it.label }
            .takeWhile { it != 1 }
            .joinToString("")
    }

    part2(parse) {
        val (cup, one) = sequence {
            yieldAll(it)
            var n = it.max() + 1
            while (true) yield(n++)
        }.take(1_000_000).asIterable().toCups()

        cup.move(10_000_000)
        one.nextClockwise.let { it.label.toLong() * it.nextClockwise.label }
    }
}) {

    class Cup(val label: Int) {
        var nextClockwise: Cup = this
        var previousByLabel: Cup = this
    }

    fun Cup.move(n: Int = 1): Cup {
        var current = this
        repeat(n) {
            val a = current.nextClockwise
            val b = a.nextClockwise
            val c = b.nextClockwise
            current.nextClockwise = c.nextClockwise

            var dest = current.previousByLabel
            while (dest === a || dest === b || dest === c) dest = dest.previousByLabel

            c.nextClockwise = dest.nextClockwise
            dest.nextClockwise = a
            current = current.nextClockwise
        }
        return current
    }

    fun Iterable<Int>.toCups(): Pair<Cup, Cup> {
        val cups = map(::Cup).also {
            it.zipWithNext { a, b -> a.nextClockwise = b }
            it.last().nextClockwise = it.first()
        }
        val min = cups.sortedBy { it.label }.also {
            it.zipWithNext { a, b -> b.previousByLabel = a }
            it.first().previousByLabel = it.last()
        }.first()
        return cups.first() to min
    }
}