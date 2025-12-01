package year2024

import aok.PuzzleInput
import aok.checkAll
import aok.lines
import aok.solveAll
import aok.warmup
import aoksp.AoKSolution
import year2024.Day08Tidy.solve

@AoKSolution
object Day08 {
    context(_: PuzzleInput)
    fun part1(): Int {
        val yr = lines.indices
        val xr = lines.first().indices
        return buildSet {
            buildMap<Char, MutableList<Pair<Int, Int>>> {
                lines.forEachIndexed { y, s ->
                    s.forEachIndexed { x, c ->
                        if (c != '.') {
                            val others = getOrPut(c, ::mutableListOf)
                            others.forEach { (ox, oy) ->
                                val dx = ox - x
                                val dy = oy - y
                                val an1 = (x - dx) to (y - dy)
                                add(an1)
                                val an2 = (ox + dx) to (oy + dy)
                                add(an2)
                            }
                            others.add(x to y)
                        }
                    }
                }
            }
        }.count { (x, y) ->
            x in xr && y in yr
        }
    }

    context(_: PuzzleInput)
    fun part2(): Int {
        val yr = lines.indices
        val xr = lines.first().indices
        return buildSet {
            buildMap<Char, MutableList<Pair<Int, Int>>> {
                lines.forEachIndexed { y, s ->
                    s.forEachIndexed { x, c ->
                        if (c != '.') {
                            val others = getOrPut(c, ::mutableListOf)
                            others.forEach { (ox, oy) ->
                                val dx = ox - x
                                val dy = oy - y

                                var ax = x
                                var ay = y
                                while (ay in yr && ax in xr) {
                                    add(ax to ay)
                                    ay -= dy
                                    ax -= dx
                                }
                                ax = ox
                                ay = oy
                                while (ay in yr && ax in xr) {
                                    add(ax to ay)
                                    ay += dy
                                    ax += dx
                                }
                            }
                            others.add(x to y)
                        }
                    }
                }
            }
        }.count { (x, y) ->
            x in xr && y in yr
        }
    }
}

@AoKSolution
object Day08Tidy {
    context(input: PuzzleInput)
    fun part1() = input.solve { a, b ->
        val delta = a - b
        invoke(b - delta)
        invoke(a + delta)
    }

    context(input: PuzzleInput)
    fun part2(): Int = input.solve { a, b ->
        val delta = a - b
        var node = a
        while (invoke(node)) node -= delta
        node = b
        while (invoke(node)) node += delta
    }

    private data class Pos(val x: Int, val y: Int) {
        operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
        operator fun minus(other: Pos) = Pos(x - other.x, y - other.y)
    }

    private fun PuzzleInput.valid(pos: Pos) = pos.y in lines.indices && pos.x in lines.indices // square?

    private inline fun PuzzleInput.solve(antinodes: ((Pos) -> Boolean).(Pos, Pos) -> Unit): Int = buildSet<Pos> {
        buildMap<_, MutableList<Pos>> {
            lines.forEachIndexed { y, s ->
                s.forEachIndexed { x, c ->
                    if (c != '.') {
                        val new = Pos(x, y)
                        val others = getOrPut(c, ::mutableListOf)
                        others.forEach { other ->
                            antinodes({
                                when {
                                    this@solve.valid(it) -> add(it).let { true }
                                    else -> false
                                }
                            }, new, other)
                        }
                        others.add(Pos(x, y))
                    }
                }
            }
        }
    }.size
}

@AoKSolution
object Day08Inline {
    context(input: PuzzleInput)
    fun part1() = input.solve()

    context(input: PuzzleInput)
    fun part2(): Int = input.solve(true)

    private data class Pos(val x: Int, val y: Int) {
        operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
        operator fun minus(other: Pos) = Pos(x - other.x, y - other.y)
    }

    private fun PuzzleInput.valid(pos: Pos) = pos.y in lines.indices && pos.x in lines.indices // square?

    private fun PuzzleInput.solve(resonant: Boolean = false): Int {
        val visited = mutableSetOf<Pos>()
        fun visit(node: Pos) = valid(node).also { if (it) visited.add(node) }
        val antennae = mutableMapOf<Char, MutableList<Pos>>()
        for ((y, s) in lines.withIndex()) {
            for ((x, c) in s.withIndex()) {
                if (c != '.') {
                    val new = Pos(x, y)
                    val others = antennae.getOrPut(c, ::mutableListOf)
                    for (other in others) {
                        val delta = other - new
                        if (resonant) {
                            var node = new
                            while (visit(node)) node -= delta
                            node = other
                            while (visit(node)) node += delta
                        } else {
                            visit(new - delta)
                            visit(other + delta)
                        }
                    }
                    others.add(Pos(x, y))
                }
            }
        }
        return visited.size
    }
}

fun main() {
    queryDay(8)
        .checkAll(14, 34) {
            """
            ............
            ........0...
            .....0......
            .......0....
            ....0.......
            ......A.....
            ............
            ............
            ........A...
            .........A..
            ............
            ............
            """.trimIndent()
        }
        .warmup(2.0, window = 100)
        .solveAll()
}
