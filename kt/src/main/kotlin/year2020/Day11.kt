package year2020

import aok.Parser
import aok.PuzDSL
import aoksp.AoKSolution
import year2019.Day24.Counter.Companion.count

fun main() = solveDay(
    11,
//    input = aok.InputProvider.Example
)

@AoKSolution
object Day11 : PuzDSL({

    part1(Seating) { seating ->
        generateSequence(seating) {
            it.map { x, y, c ->
                when (c) {
                    'L' -> if (it.occupiedAdjacent(x, y) > 0) 'L' else '#'
                    '#' -> if (it.occupiedAdjacent(x, y) < 4) '#' else 'L'
                    else -> c
                }
            }
        }
            .zipWithNext { a, b -> a.takeIf(b::equals) }
            .firstNotNullOf { it?.occupiedSeats }
    }

    part2(Seating) { seating ->
        generateSequence(seating) {
            it.map { x, y, c ->
                when (c) {
                    'L' -> if (it.occupiedVisible(x, y) > 0) 'L' else '#'
                    '#' -> if (it.occupiedVisible(x, y) < 5) '#' else 'L'
                    else -> c
                }
            }
        }
            .zipWithNext { a, b -> a.takeIf(b::equals) }
            .firstNotNullOf { it?.occupiedSeats }
    }
}) {
    @JvmInline
    value class Seating(private val rows: List<String>) {
        val occupiedSeats get() = rows.sumOf { it.count('#'::equals) }
        operator fun get(x: Int, y: Int) = rows.getOrNull(y)?.getOrNull(x)
        fun map(transform: (x: Int, y: Int, c: Char) -> Char) = Seating(rows.mapIndexed { y, s ->
            s.mapIndexed { x, c -> transform(x, y, c) }.joinToString("")
        })

        override fun toString() = rows.joinToString("\n")

        fun occupiedAdjacent(x: Int, y: Int) = count {
            forEachDirection { dx, dy ->
                incIf(get(x + dx, y + dy) == '#')
            }
        }

        fun occupiedVisible(x: Int, y: Int) = count {
            forEachDirection { dx, dy ->
                var nx = x + dx
                var ny = y + dy
                while (get(nx, ny) == '.') {
                    nx += dx
                    ny += dy
                }
                incIf(get(nx, ny) == '#')
            }
        }

        companion object : Parser<Seating> by Parser({ Seating(lines) })
    }

    private fun forEachDirection(block: (dx: Int, dy: Int) -> Unit) {
        for (dy in -1..1)
            for (dx in -1..1)
                if (dx != 0 || dy != 0)
                    block(dx, dy)
    }
}

