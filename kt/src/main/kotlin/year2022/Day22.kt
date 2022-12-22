@file:OptIn(ExperimentalStdlibApi::class)

package year2022

import InputScopeProvider
import aok.PuzzleInput
import aoksp.AoKSolution
import queryPuzzles
import solveAll
import year2022.Day22.Direction.*

fun main(): Unit = with(InputScopeProvider.Example) {
    queryPuzzles { year == 2022 && day == 22 }
//        .warmupEach(5.seconds)
        .solveAll(runIterations = 1)
}

@AoKSolution
object Day22 {

    context(PuzzleInput)
    fun part1(): Int {
        val (board, moves) = parse(LayoutInterpreter.Linear)
        return with(board) {
            moves.fold(Pos()) { pos, move -> pos.move(move) }.password()
        }
    }

    context(PuzzleInput)
    fun part2(): Int {
        val (board, moves) = parse(LayoutInterpreter.Cubic)
        return with(board) {
            moves.fold(Pos()) { pos, move -> pos.move(move) }.password()
        }
    }

    context(PuzzleInput)
    private fun parse(layoutInterpreter: LayoutInterpreter) = input.split("\n\n").let { (grid, path) ->
        parseBoard(grid, layoutInterpreter) to parseMoves(path)
    }

    private fun parseMoves(path: String) = sequence {
        "[0-9]+|R|L".toRegex().findAll(path).forEach {
            when (it.value) {
                "L" -> yield(Move.TurnLeft)
                "R" -> yield(Move.TurnRight)
                else -> repeat(it.value.toInt()) { yield(Move.Forward) }
            }
        }
    }

    private fun parseBoard(grid: String, layoutInterpreter: LayoutInterpreter): Board {
        val gridLines = grid.lines()
        val cols = gridLines.maxOf(String::length)

        val fieldSize = gridLines.flatMap { it.asIterable().contiguousCounts { it != ' ' } }
            .plus((0..<cols).flatMap { c -> gridLines.contiguousCounts { c in it.indices && it[c] != ' ' } })
            .min()

        val walls = buildList {
            for (y in gridLines.indices step fieldSize) {
                for (x in 0..<cols step fieldSize) {
                    val populated = gridLines[y].getOrNull(x)?.takeUnless { it == ' ' } != null
                    if (populated) {
                        add(gridLines.subList(y, y + fieldSize).flatMap {
                            it.substring(x, x + fieldSize).map { it == '#' }
                        }.toBooleanArray())
                    }
                }
            }
        }

        val mapLayout = buildList {
            var f = 0
            for (y in gridLines.indices step fieldSize) {
                add(buildString {
                    for (x in 0..<cols step fieldSize) {
                        val populated = gridLines[y].getOrNull(x)?.takeUnless { it == ' ' } != null
                        append(if (populated) f++ else '-')
                    }
                })
            }
        }


        return Board(fieldSize, mapLayout, walls, layoutInterpreter.interpretLinks(mapLayout))
    }

    private enum class Direction { Left, Right, Up, Down }

    private fun Direction.turnLeft() = when (this) {
        Up -> Left
        Left -> Down
        Down -> Right
        Right -> Up
    }

    private fun Direction.turnRight() = when (this) {
        Up -> Right
        Right -> Down
        Down -> Left
        Left -> Up
    }

    private enum class Move { Forward, TurnLeft, TurnRight }
    private data class Pos(val field: Int = 0, val x: Int = 0, val y: Int = 0, val facing: Direction = Right)

    private class Board(
        val fieldSize: Int,
        val layout: List<String>,
        val walls: List<BooleanArray>,
        val links: List<FieldLink>,
    ) {
        fun Pos.move(move: Move) = when (move) {
            Move.Forward -> move()
            Move.TurnLeft -> copy(facing = facing.turnLeft())
            Move.TurnRight -> copy(facing = facing.turnRight())
        }

        private fun Pos.move(): Pos = when (facing) {
            Up -> if (y > 0) copy(y = y - 1) else teleport()
            Down -> if (y + 1 < fieldSize) copy(y = y + 1) else teleport()
            Left -> if (x > 0) copy(x = x - 1) else teleport()
            Right -> if (x + 1 < fieldSize) copy(x = x + 1) else teleport()
        }.takeUnless { (f, x, y) -> walls[f][y * fieldSize + x] } ?: this

        private fun Pos.teleport(): Pos {
            val (destField, destEdge) = findLink(field, facing)
            val newFace = when (destEdge) {
                Up -> Down
                Down -> Up
                Left -> Right
                Right -> Left
            }
            return when (facing to destEdge) {
                Down to Up, Up to Down ->
                    copy(field = destField, y = fieldSize - y - 1)

                Left to Right, Right to Left ->
                    copy(field = destField, x = fieldSize - x - 1)

                Up to Right, Right to Up, Down to Left, Left to Down ->
                    copy(field = destField, x = fieldSize - y - 1, y = fieldSize - x - 1, facing = newFace)

                Up to Left, Left to Up, Right to Down, Down to Right ->
                    copy(field = destField, x = y, y = x, facing = newFace)

                Down to Down, Up to Up ->
                    copy(field = destField, x = fieldSize - x - 1, facing = newFace)

                Left to Left, Right to Right ->
                    copy(field = destField, y = fieldSize - y - 1, facing = newFace)

                else -> error("can't teleport from $field to $destField ($facing to $destEdge)")
            }
        }

        private fun findLink(fromField: Int, direction: Direction): Pair<Int, Direction> =
            links.firstOrNull { it.fieldA == fromField && it.edgeA == direction }?.let { it.fieldB to it.edgeB }
                ?: links.first { it.fieldB == fromField && it.edgeB == direction }.let { it.fieldA to it.edgeA }

        fun Pos.password(): Int {
            val f = field.digitToChar()
            val fy = layout.indexOfFirst { f in it }
            val fx = layout[fy].indexOf(f)

            val py = 1 + y + (fy * fieldSize)
            val px = 1 + x + (fx * fieldSize)
            val pf = when (facing) {
                Right -> 0
                Down -> 1
                Left -> 2
                Up -> 3
            }

            return 1000 * py + 4 * px + pf
        }
    }

    private sealed interface LayoutInterpreter {
        fun interpretLinks(layout: List<String>): List<FieldLink>

        object Linear : LayoutInterpreter {
            private fun List<Int>.link(from: Direction, to: Direction) =
                zipWithNext { a, b -> FieldLink(a, from, to, b) } + FieldLink(last(), from, to, first())

            override fun interpretLinks(layout: List<String>): List<FieldLink> {
                val rowLinks = layout.flatMap { row ->
                    row.mapNotNull { it.digitToIntOrNull() }.link(Right, Left)
                }
                val colLinks = layout.first().indices.flatMap { i ->
                    layout.mapNotNull { it[i].digitToIntOrNull() }.link(Down, Up)
                }
                return (rowLinks + colLinks)
            }
        }

        object Cubic : LayoutInterpreter {
            // todo: generate links based on cube net. eta: never.
            private val netLinks: Map<String, String> = mapOf(
                "--0-:123-:--45" to "0DU3;0LU2;0RR5;0UU1;1DD4;1LD5;1RL2;2DL4;2RL3;3DU4;3RU5;4RL5", // example
                "-01:-2-:34-:5--" to "0DU2;0LL3;0RL1;0UL5;1DR2;1RR4;1UD5;2DU4;2LU3;3DU5;3RL4;4DR5", // input
            )

            override fun interpretLinks(layout: List<String>) =
                FieldLink.of(netLinks[layout.joinToString(":")] ?: error("no links defined for '$layout'"))
        }
    }

    private data class FieldLink(val fieldA: Int, val edgeA: Direction, val edgeB: Direction, val fieldB: Int) {
        companion object {
            private fun Char.toDirection() = when (uppercaseChar()) {
                'U' -> Up
                'D' -> Down
                'L' -> Left
                'R' -> Right
                else -> error("unknown direction $this")
            }

            fun of(linksString: String) = linksString.split(';').map {
                FieldLink(it[0].digitToInt(), it[1].toDirection(), it[2].toDirection(), it[3].digitToInt())
            }
        }
    }

    private fun <T> Iterable<T>.contiguousCounts(predicate: (T) -> Boolean) = sequence {
        var c = 0
        forEach { v ->
            if (predicate(v)) c++
            else if (c > 0) {
                yield(c)
                c = 0
            }
        }
        if (c > 0) yield(c)
    }

    @Suppress("unused")
    private fun Board.debug(
        vararg markers: Pair<Pos, String>,
        void: String = "▪️",
        wall: String = "⬜️",
        blank: String = "⬛️",
    ) {
        fun markerAt(f: Int, x: Int, y: Int) = markers.firstNotNullOfOrNull { (pos, marker) ->
            marker.takeIf { pos.field == f && pos.x == x && pos.y == y }
        }
        layout.forEach { fr ->
            repeat(fieldSize) { y ->
                println(fr.map(Char::digitToIntOrNull).joinToString("") { field ->
                    (0..<fieldSize).joinToString("") { x ->
                        field?.let {
                            markerAt(field, x, y) ?: if (walls[field][y * fieldSize + x]) wall else blank
                        } ?: void
                    }
                })
            }
        }
    }
}
