package year2024

import aok.PuzDSL
import aok.PuzzleInput
import aok.checkAll
import aok.lines
import aok.solveAll
import aok.warmupEach
import aoksp.AoKSolution
import utils.forEachCharIndexed
import utils.sumOfEachCharIndexed
import java.util.*
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day10 : PuzDSL({
    data class Loc(val x: Int, val y: Int) {
        val neighbours
            get() = listOf(
                Loc(x - 1, y),
                Loc(x + 1, y),
                Loc(x, y - 1),
                Loc(x, y + 1)
            )
    }

    operator fun List<String>.contains(it: Loc) = it.y in indices && it.x in indices // assumes square
    operator fun List<String>.get(it: Loc) = getOrNull(it.y)?.getOrNull(it.x)

    fun PuzzleInput.trailCount(start: Loc, allRoutes: Boolean = false): Int {
        val visited = mutableSetOf<Loc>()
        val locs = mutableListOf(start)
        var trails = 0
        while (locs.isNotEmpty()) {
            val at = locs.removeLast()
            val height = lines[at.y][at.x].inc()
            for (next in at.neighbours)  {
                if(lines[next] == height && (allRoutes || visited.add(next))) {
                    if (height == '9') trails++ else locs += next
                }
            }
        }
        return trails
    }

    fun PuzzleInput.solve(allRoutes: Boolean = false) =
        lines.sumOfEachCharIndexed { x, y, c ->
            if (c == '0') trailCount(Loc(x, y), allRoutes) else 0
        }


    part1 { solve(false) }

    part2 { solve(true) }
})

@AoKSolution
object Day10Bitty {
    enum class Dir { U, L, D, R }

    @JvmInline
    value class Loc(val raw: Int) {
        constructor(x: Int, y: Int) : this(y.inc() shl BIT_WIDTH or x)

        operator fun plus(dir: Dir) = when (dir) {
            Dir.U -> Loc(raw - Y_INC)
            Dir.D -> Loc(raw + Y_INC)
            Dir.L -> Loc(raw - 1)
            Dir.R -> Loc(raw + 1)
        }

        companion object {
            const val BIT_WIDTH = 6
            const val Y_MASK = (-1 shl BIT_WIDTH).inv()
            private const val Y_INC = 1.shl(BIT_WIDTH)
        }
    }

    @JvmInline
    value class Route(val raw: Int = 0) {
        operator fun plus(dir: Dir): Route = Route(raw.shl(2).or(dir.ordinal))
    }

    @JvmInline
    value class HeightMap(val maps : Array<BitSet>) {
        inline fun BitSet.forEach(block: (Int) -> Unit) {
            var i = nextSetBit(0)
            while(i >= 0) {
                block(i)
                i = nextSetBit(i + 1)
            }
        }
        inline fun forEach(height: Int = 0, block: (Loc) -> Unit) {
            maps[height].forEach { index -> block(Loc(index)) }
        }
        operator fun get(height: Int, loc: Loc) = maps[height][loc.raw]
        operator fun set(height: Int, loc: Loc, bit: Boolean) { maps[height][loc.raw] = bit }
    }

    private fun HeightMap.routesFrom(at: Loc, height: Int = 1, route: Route = Route(), found: (Route, Loc) -> Unit) {
        for (dir in Dir.entries) {
            val n = at + dir
            if (get(height, n)) {
                val r = route + dir
                if (height == 9) found(r, n) else routesFrom(n, height+1, r, found)
            }
        }
    }

    private fun List<String>.heightMap() = HeightMap(Array(10) { BitSet(Loc.Y_MASK.shl(Loc.BIT_WIDTH).or(Loc.Y_MASK)) })
        .also {
            forEachCharIndexed { x, y, c -> it.maps[c.digitToInt()][Loc(x, y).raw] = true }
        }

    context(_: PuzzleInput)
    private fun solve(unique: Boolean = false): Int {
        val map = lines.heightMap()
        return buildSet {
            map.forEach(height = 0) { start ->
                map.routesFrom(start) { route, loc ->
                    add((if (unique) route.raw else loc.raw) shl 12 or start.raw)
                }
            }
        }.size
    }

    context(_: PuzzleInput)
    fun part1() = solve()

    context(_: PuzzleInput)
    fun part2() = solve(true)
}

fun main() = queryDay(10)
    .checkAll(
        36, 81,
        true, {
            """
        89010123
        78121874
        87430965
        96549874
        45678903
        32019012
        01329801
        10456732
    """.trimIndent()
        })
    .checkAll(538, 1110)
//    .warmup(sigma = 1.3, window = 30)
    .warmupEach(5.seconds)
    .solveAll(100)
