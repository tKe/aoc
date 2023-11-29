package year2020

import aok.PuzDSL
import aoksp.AoKSolution
import year2020.Day20.countMatches
import year2020.Day20.zipConcat

fun main() = solveDay(
    20,
//    input = aok.InputProvider.Example
)

@AoKSolution
object Day20 : PuzDSL({
    val parse = parser {
        input.splitToSequence("\n\n").associate {
            val lines = it.lines()
            val tile = lines.first().trim(*"Tile :".toCharArray()).toInt()
            tile to Tile(lines.drop(1))
        }
    }

    fun String.canonical() = listOf(this, reversed()).min()
    fun Map<Int, Tile>.edgeMap() = flatMap { (id, tile) -> tile.edges.map { it.canonical() to id } }
        .groupingBy { it.first }.fold(emptySet<Int>()) { acc, (_, id) -> acc + id }
        .filterValues { it.size == 2 }

    part1(parse) { tiles ->
        val edgeMap = tiles.edgeMap()
        tiles.filterValues { it.edges.count { edge -> edge.canonical() !in edgeMap } == 2 }
            .keys.fold(1L, Long::times)
    }

    fun Map<Int, Tile>.merge(): Tile {
        val edgeMap = edgeMap()

        val topLeft = entries.first { (_, tile) -> tile.edges.count { it.canonical() !in edgeMap } == 2 }
            .let { (id, it) ->
                var tile = it
                if (tile.top.canonical() in edgeMap) tile = tile.flip(y = true)
                if (tile.left.canonical() in edgeMap) tile = tile.flip(x = true)
                id to tile
            }

        fun Pair<Int, Tile>.chain(linkage: Pair<Tile.() -> String, Tile.() -> String>) =
            generateSequence(this) { (id, from) ->
                val match = edgeMap[linkage.first(from).canonical()] ?: return@generateSequence null
                val nextTile = match.single { it != id }
                nextTile to getValue(nextTile).orientations.first { linkage.first(from) == linkage.second(it) }
            }.toList()


        return Tile(topLeft.chain(Tile::bottom to Tile::top).flatMap { left ->
            left.chain(Tile::right to Tile::left)
                .map { (_, tile) -> tile.stripped }
                .zipConcat()
        })
    }

    part2(parse) { tiles ->
        val monster = """
                |                  # 
                |#    ##    ##    ###
                | #  #  #  #  #  #   
            """.trimMargin()

        val habitat = tiles.merge()
        val monsters = monster.lines().let {
            habitat.orientations.maxOf { (content) -> content.countMatches(it) }
        }
        // assumes monsters don't overlap
        habitat.content.sumOf { it.count('#'::equals) } - monsters * monster.count('#'::equals)
    }

}) {
    data class Tile(val content: List<String>) {
        val top by lazy(content::first)
        val bottom by lazy(content::last)
        val left by lazy { content.map(String::first).joinToString("") }
        val right by lazy { content.map(String::last).joinToString("") }
        val edges by lazy { listOf(top, right, bottom, left) }

        val orientations = sequence {
            repeat(8) {
                yield(flip(it and 1 == 1, it and 2 == 2, it and 4 == 4))
            }
        }

        val stripped by lazy { content.subList(1, content.lastIndex).map { it.substring(1, it.lastIndex) } }

        fun flip(x: Boolean = false, y: Boolean = false, swapXY: Boolean = false): Tile {
            var content = content
            if (x) content = content.map { it.reversed() }
            if (y) content = content.asReversed()
            if (swapXY) content = content.first().indices.map { cx ->
                content.joinToString("") { "${it[cx]}" }
            }
            return copy(content = content)
        }
    }

    fun Iterable<List<String>>.zipConcat(sep: String = "") =
        reduce { acc: List<String>, next: List<String> -> acc.mapIndexed { i, s -> s + sep + next[i] } }

    fun List<String>.countMatches(templates: List<String>): Int {
        fun String.matches(line: String, offset: Int) =
            asSequence().zip(line.subSequence(offset, offset + length).asSequence())
                .all { (a, b) -> a == ' ' || a == b }

        fun matchesAt(x: Int, y: Int) =
            templates.withIndex().all { (dy, template) -> template.matches(get(y + dy), offset = x) }

        var matches = 0
        for (y in 0..size - templates.size)
            for (x in 0..first().length - templates.maxOf { it.length })
                if (matchesAt(x, y)) matches++
        return matches
    }
}