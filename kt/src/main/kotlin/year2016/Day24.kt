package year2016

import aok.PuzDSL
import aok.PuzzleInput
import aok.Warmup
import aoksp.AoKSolution
import utils.bfs
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day24 : PuzDSL({
    fun String.indicesOf(predicate: (Char) -> Boolean): Set<Int> =
        mapIndexedNotNullTo(mutableSetOf()) { i, c -> i.takeIf { predicate(c) } }

    fun <T> Collection<T>.permutations(): Sequence<List<T>> = sequence {
        forEach { next ->
            if (size == 1) yield(listOf(next))
            else yieldAll(minus(next).permutations().map { listOf(next) + it })
        }
    }

    fun Collection<Int>.orderedPairs() =
        sequence { forEach { a -> forEach { b -> if (a < b) yield(a to b) } } }

    fun PuzzleInput.visitPois(returnToStart: Boolean = false): Int {
        val chars = input
        val width = lines.first().length + 1

        val passable = chars.indicesOf { it != '#' }
        fun Int.moves() = sequenceOf(inc(), dec(), plus(width), minus(width)).filter(passable::contains)

        val start = chars.indexOf('0')
        val pois = chars.indicesOf { it.isDigit() }

        val distances = pois.orderedPairs().associateWith { (a, b) -> bfs(a, b::equals, Int::moves) }
        infix fun Int.distanceTo(other: Int) = distances.getValue(if (this < other) this to other else other to this)

        // todo: non-brute-force?
        return (pois - start).permutations().minOf { route ->
            route.zipWithNext { a, b -> a distanceTo b }.sum()
                .plus(start distanceTo route.first())
                .plus(if (returnToStart) route.last() distanceTo start else 0)
        }
    }

    part1 { visitPois() }
    part2 { visitPois(returnToStart = true) }
})

fun main() = solveDay(
    24,
    runs = 20, warmup = Warmup.eachFor(8.seconds)
)
