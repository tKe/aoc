package year2020

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(24)

@AoKSolution
object Day24 : PuzDSL({
    val parserCells = lineParser { HexCell.afterMoves(it) }

    fun Iterable<HexCell>.toggle() = buildSet { for (cell in this@toggle) if (!add(cell)) remove(cell) }

    part1(parserCells) {
        it.toggle().size
    }

    part2(parserCells) {
        generateSequence(it.toggle()) { blacks ->
            buildSet {
                val whites = mutableSetOf<HexCell>()
                for(cell in blacks) {
                    val (nb, nw) = cell.neighbours.partition(blacks::contains)
                    whites += nw
                    if (nb.size in 1..2) add(cell)
                }
                addAll(whites.filter { white -> white.neighbours.count(blacks::contains) == 2 })
            }
        }.elementAt(100).size
    }
}) {
    enum class HexMove {
        East, SouthEast, SouthWest, West, NorthWest, NorthEast,
        ;

        companion object {
            fun moves(str: String) = sequence {
                val i = str.iterator()
                while (i.hasNext()) yield(
                    when (i.nextChar()) {
                        'e' -> East
                        'w' -> West
                        'n' -> when (i.nextChar()) {
                            'e' -> NorthEast
                            'w' -> NorthWest
                            else -> error("💥")
                        }

                        's' -> when (i.nextChar()) {
                            'e' -> SouthEast
                            'w' -> SouthWest
                            else -> error("💥")
                        }

                        else -> error("💥")
                    }
                )
            }.asIterable()
        }
    }

    data class HexCell(val q: Int = 0, val r: Int = 0) {
        val x = q + (r + (r and 1)) / 2
        val y = r

        val neighbours by lazy { HexMove.entries.map(::plus) }

        companion object {
            fun fromXY(col: Int, row: Int) = HexCell(
                q = col - (row + (row and 1)) / 2,
                r = row,
            )

            fun afterMoves(moves: String, start: HexCell = HexCell()) = start + HexMove.moves(moves)
        }

        operator fun plus(move: HexMove) = when (move) {
            HexMove.NorthEast -> copy(q = q + 1, r = r + -1)
            HexMove.SouthWest -> copy(q = q + -1, r = r + 1)
            HexMove.NorthWest -> copy(r = r + -1)
            HexMove.SouthEast -> copy(r = r + 1)
            HexMove.West -> copy(q = q + -1)
            HexMove.East -> copy(q = q + 1)
        }

        operator fun plus(moves: Iterable<HexMove>): HexCell = moves.fold(this, HexCell::plus)
    }

    fun Set<HexCell>.print(vararg highlights: Pair<(HexCell) -> Boolean, String>, border: Int = 2) {
        val (ys, xs) = if (isNotEmpty()) (minOf(HexCell::y) - border..maxOf(HexCell::y) + border) to
                (minOf(HexCell::x) - border..maxOf(HexCell::x) + border)
        else (-border..border).let { it to it }

        for (y in ys) {
            if (y % 2 == 0) print(" ")
            for (x in xs) {
                val cell = HexCell.fromXY(x, y)
                print(
                    highlights.firstOrNull { it.first(cell) }?.second ?: when {
                        contains(cell) -> "⚫"
                        else -> "⚪"
                    }
                )
            }
            println()
        }
    }
}