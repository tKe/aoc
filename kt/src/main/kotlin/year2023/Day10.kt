package year2023

import aok.PuzDSL
import aoksp.AoKSolution
import year2023.Day10.Direction.*

fun main() = solveDay(
    10,
//    warmup = aok.Warmup.iterations(10), runs = 3,
//    input = aok.InputProvider.Example,
)

@AoKSolution
object Day10 : PuzDSL({
    val parser = parser {
        val start = lines.indexOfFirst { 'S' in it }
            .let { Int2(lines[it].indexOf('S'), it) }
        val pipes = DenselyPackedPipes(lines)
        start to pipes
    }

    fun DenselyPackedPipes.loop(start: Int2) = buildSet {
        val pending = ArrayDeque(listOf(start))
        while (pending.isNotEmpty())
            pending.removeFirst().connectedNeighbours()
                .filter(::add)
                .forEach(pending::add)
    }

    part1(parser) { (start, pipes) ->
        pipes.loop(start).size / 2
    }

    part2(parser) { (start, pipes) ->
        val loop = pipes.loop(start)
        val expanded = buildSet(loop.size * 2) {
            for ((x, y) in loop) {
                val center = Int2(x * 2, y * 2)
                add(center)
                for (it in pipes[x, y]?.openings.orEmpty()) {
                    add(center + it)
                }
            }
        }

        val rngX = loop.minOf { it.x }..<loop.maxOf { it.x }
        val rngY = loop.minOf { it.y }..<loop.maxOf { it.y }

        val expX = rngX.first - 1..2 * (rngX.last + 2)
        val expY = rngY.first - 1..2 * (rngY.last + 2)
        val pending = ArrayDeque(listOf(Int2(expX.first, expY.first)))
        val visited = mutableSetOf(pending.first())
        visited += expanded // add the expanded loop
        while (pending.isNotEmpty()) {
            val next = pending.removeFirst()
            Direction.entries.map { next + it }
                .filter { it.x in expX && it.y in expY }
                .filter { visited.add(it) }
                .forEach { pending += it }
        }

        rngY.sumOf { y -> rngX.count { Int2(it shl 1, y shl 1) !in visited } }
    }
}) {
    data class Int2(val x: Int, val y: Int) {
        operator fun plus(dir: Direction) = when (dir) {
            N -> copy(y = y - 1)
            S -> copy(y = y + 1)
            E -> copy(x = x + 1)
            W -> copy(x = x - 1)
        }
    }

    enum class Direction {
        N, S, E, W;

        val opposite by lazy {
            when (this) {
                N -> S
                S -> N
                E -> W
                W -> E
            }
        }
    }

    enum class Pipe(val openings: Set<Direction>) {
        NS(N, S), EW(E, W),
        NE(N, E), NW(N, W),
        SE(S, E), SW(S, W),
        Entrance(Direction.entries)
        ;

        constructor(vararg openings: Direction) : this(openings.toSet())
        constructor(openings: Collection<Direction>) : this(openings.toSet())

        operator fun contains(end: Direction) = end in openings
    }

    @JvmInline
    value class DenselyPackedPipes(private val repr: List<String>) {
        fun Int2.connectedNeighbours() = Direction.entries
            .filter { get(this)?.contains(it) ?: true }
            .mapNotNull { dir ->
                plus(dir).takeIf { get(it)?.contains(dir.opposite) ?: false }
            }

        operator fun get(loc: Int2): Pipe? = get(loc.x, loc.y)
        operator fun get(x: Int, y: Int): Pipe? = when (repr.getOrNull(y)?.getOrNull(x)) {
            '|' -> Pipe.NS
            '-' -> Pipe.EW
            'L' -> Pipe.NE
            'J' -> Pipe.NW
            '7' -> Pipe.SW
            'F' -> Pipe.SE
            'S' -> Pipe.Entrance
            else -> null
        }
    }
}
