@file:OptIn(ExperimentalStdlibApi::class)

package year2022

import aok.PuzzleInput
import aok.Warmup
import aoksp.AoKSolution
import arrow.core.padZip
import year2022.Day22.Direction.*
import kotlin.time.Duration.Companion.seconds

fun main(): Unit = solveDay(22, warmup = Warmup.eachFor(5.seconds))

@AoKSolution
object Day22 {

    context(PuzzleInput)
    fun part1(): Int {
        val (board, moves) = parse(LayoutInterpreter.Linear)
        return board.solve(moves)
    }

    context(PuzzleInput)
    fun part2(): Int {
        val (board, moves) = parse(LayoutInterpreter.Cubic)
        return board.solve(moves)
    }

    private fun Board.solve(moves: Sequence<Move>): Int {
        val path = moves.runningFold(Pos()) { pos, move -> pos.move(move) }.toList()

//        debug(
//            *path.map {
//                it to when (it.facing) {
//                    Up -> "👆🏻"
//                    Down -> "👇🏻"
//                    Left -> "👈🏻"
//                    Right -> "👉🏻"
//                }
//            }.toTypedArray(),
//            path.first() to "❇️",
//            path.last() to when (path.last().facing) {
//                Up -> "👆"
//                Down -> "👇"
//                Left -> "👈"
//                Right -> "👉"
//            }, blank = "⬛️", wall = "⬜️"
//        )

        return path.last().password()
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
                else -> yield(Move.Forward(it.value.toInt()))
            }
        }
    }

    private fun parseBoard(grid: String, layoutInterpreter: LayoutInterpreter): Board {
        val gridLines = grid.lines()
        val cols = gridLines.maxOf(String::length)

        val fieldSize = gridLines.map { line -> line.asIterable().contiguousCounts { it != ' ' }.single() }
            .plus((0..<cols).map { c -> gridLines.contiguousCounts { c in it.indices && it[c] != ' ' }.single() })
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

    private sealed class Move {
        data class Forward(val n: Int) : Move()
        object TurnLeft : Move()
        object TurnRight : Move()
    }
    private data class Pos(val field: Int = 0, val x: Int = 0, val y: Int = 0, val facing: Direction = Right)

    private class Board(
        val fieldSize: Int,
        val layout: List<String>,
        val walls: List<BooleanArray>,
        val links: List<FieldLink>,
    ) {
        fun Pos.move(move: Move) = when (move) {
            is Move.Forward -> move(move.n)
            Move.TurnLeft -> copy(facing = facing.turnLeft())
            Move.TurnRight -> copy(facing = facing.turnRight())
        }

        private fun Pos.move(n: Int) = generateSequence(this) { it.move() }.take(n).lastOrNull() ?: this

        private fun Pos.move(): Pos? = when (facing) {
            Up -> if (y > 0) copy(y = y - 1) else teleport()
            Down -> if (y + 1 < fieldSize) copy(y = y + 1) else teleport()
            Left -> if (x > 0) copy(x = x - 1) else teleport()
            Right -> if (x + 1 < fieldSize) copy(x = x + 1) else teleport()
        }.takeUnless { (f, x, y) -> walls[f][y * fieldSize + x] }

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
            override fun interpretLinks(layout: List<String>) = buildList {
                data class Edge(val field: Int, val side: Direction)

                val pending = (0..5).flatMap {
                    listOf(Up, Down, Left, Right)
                        .map { d -> Edge(it, d) }
                }.toMutableSet()

                infix fun Edge.linksTo(other: Edge) {
                    check(this in pending && other in pending) { "edges already linked!" }
                    add(FieldLink(field, side, other.side, other.field))
                    pending -= setOf(this, other)
                }

                // link layout rows
                layout.forEach { row ->
                    row.mapNotNull { it.digitToIntOrNull() }
                        .zipWithNext { a, b -> Edge(a, Right) linksTo Edge(b, Left) }
                }
                // link layout columns
                layout.first().indices.forEach { i ->
                    layout.mapNotNull { it[i].digitToIntOrNull() }
                        .zipWithNext { a, b -> Edge(a, Down) linksTo Edge(b, Up) }
                }

                fun List<FieldLink>.traverse(field: Int, edge: Direction) =
                    firstOrNull { it.fieldA == field && it.edgeA == edge }?.let { it.fieldB to it.edgeB }
                        ?: firstOrNull { it.fieldB == field && it.edgeB == edge }?.let { it.fieldA to it.edgeA }

                fun Edge.findPair(turn: (Direction) -> Direction) =
                    traverse(field, turn(side))
                        ?.let { (midField, midEdge) -> traverse(midField, turn(midEdge)) }
                        ?.let { (targetField, targetEdge) -> Edge(targetField, turn(targetEdge)) }

                fun Edge.findPair() = (findPair { it.turnLeft() } ?: findPair { it.turnRight() })?.to(this)

                while (pending.isNotEmpty()) {
                    pending.firstNotNullOf(Edge::findPair)
                        .let { (a, b) -> a linksTo b }
                }
            }

            @JvmStatic
            fun main(args: Array<String>) {
                listOf(
                    listOf("---0", "1234", "---5"),
                    listOf("---0", "1234", "-5--"),
                    listOf("--0-", "123-", "--45"),
                    listOf("--0-", "1234", "--5-"),
                    listOf("--0-", "1234", "-5--"),
                    listOf("-0--", "1234", "5---"),
                    listOf("0---", "1234", "---5"),
                    listOf("01--", "-23-", "--45"),
                    listOf("01--", "-234", "---5"),
                    listOf("01--", "-234", "--5-"),
                    listOf("012--", "--345"),
                ).forEach { net ->
                    val diagram = net.map { it.map { c -> if (c == '-') "▪️" else "⬜️" }.joinToString("") }
                    val links = interpretLinks(net).map { (fieldA, edgeA, edgeB, fieldB) ->
                        if (fieldA < fieldB) "$fieldA${edgeA.name[0]}-$fieldB${edgeB.name[0]}"
                        else "$fieldB${edgeB.name[0]}-$fieldA${edgeA.name[0]}"
                    }.sorted()

                    diagram.padZip(links.chunked(4)) { d, l ->
                        println("${(l?.toString() ?: "").padEnd(28)}  ${d ?: ""}")
                    }
                    println()
                }
            }
        }
    }

    private data class FieldLink(val fieldA: Int, val edgeA: Direction, val edgeB: Direction, val fieldB: Int)

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
        fun markerAt(f: Int, x: Int, y: Int) = markers.lastOrNull { (pos, marker) ->
            pos.field == f && pos.x == x && pos.y == y
        }?.second
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
