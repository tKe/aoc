package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import utils.bfs

fun main() = solveDay(
    20,
//    input = aok.InputProvider.Example
)

@AoKSolution
object Day20 : PuzDSL({

    val parseDonut = parser {
        buildMap {
            fun char(x: Int, y: Int) = lines.getOrNull(y)?.getOrNull(x) ?: ' '
            for ((y, line) in lines.withIndex()) for ((x, c) in line.withIndex()) when (c) {
                '.' -> putIfAbsent(Int2(x, y), Corridor) // don't overwrite portals from above or left
                in 'A'..'Z' -> {
                    val u = char(x, y - 1)
                    val d = char(x, y + 1)
                    val l = char(x - 1, y)
                    val r = char(x + 1, y)
                    when {
                        d == '.' && u in 'A'..'Z' -> put(Int2(x, y + 1), Portal("$u$c"))
                        u == '.' && d in 'A'..'Z' -> put(Int2(x, y - 1), Portal("$c$d"))
                        r == '.' && l in 'A'..'Z' -> put(Int2(x + 1, y), Portal("$l$c"))
                        l == '.' && r in 'A'..'Z' -> put(Int2(x - 1, y), Portal("$c$r"))
                    }
                }
            }
        }
    }

    fun Map<Int2, Cell>.links() = asSequence().filter { it.value is Portal }
        .groupBy({ it.value }, { it.key }).let {
            buildMap {
                for ((_, locs) in it) {
                    when (locs.size) {
                        2 -> {
                            val (a, b) = locs
                            put(a, b)
                            put(b, a)
                        }
                    }
                }
            }
        }

    fun Map<Int2, Cell>.findPortal(id: String) = sequence {
        for ((pos, cell) in entries) {
            if (cell is Portal && cell.id == id) yield(pos)
        }
    }

    part1(parseDonut) { donut ->
        val portals = donut.links()
        val aa = donut.findPortal("AA").single()
        val zz = donut.findPortal("ZZ").single()

        bfs(aa, zz::equals) {
            neighbours.filter(donut::containsKey) + listOfNotNull(portals[this@bfs])
        }
    }

    fun Map<Int2, Int2>.partition(): Pair<Map<Int2, Int2>, Map<Int2, Int2>> {
        val tl = Int2(keys.minOf(Int2::x), keys.minOf(Int2::y))
        val br = Int2(keys.maxOf(Int2::x), keys.maxOf(Int2::y))
        val outer = filterKeys { (x, y) -> x == tl.x || y == tl.y || x == br.x || y == br.y }
        val inner = minus(outer.keys)
        return inner to outer
    }

    part2(parseDonut) { donut ->
        val aa = donut.findPortal("AA").single()
        val zz = donut.findPortal("ZZ").single()
        val (inner, outer) = donut.links().partition()

        bfs(aa to 0, (zz to 0)::equals) {
            val (pos, layer) = this
            pos.neighbours.filter(donut::containsKey).map { it to layer } + listOfNotNull(
                inner[pos]?.to(layer + 1),
                if(layer > 0) outer[pos]?.to(layer - 1) else null
            )
        }
    }
}) {
    data class Int2(val x: Int, val y: Int) {
        val neighbours get() = sequenceOf(copy(x = x - 1), copy(x = x + 1), copy(y = y - 1), copy(y = y + 1))
    }
    sealed interface Cell
    data object Corridor : Cell
    data class Portal(val id: String) : Cell {
        override fun toString() = id
    }
}
