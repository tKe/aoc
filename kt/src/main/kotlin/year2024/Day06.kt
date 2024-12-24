package year2024

import aok.InputProvider
import aok.PuzzleInput
import aok.checkAll
import aok.solveAll
import aok.warmup
import aoksp.AoKSolution
import arrow.fx.coroutines.parMap

@AoKSolution
object Day06Fast {
    context(PuzzleInput)
    fun part1() = visitWorld { buildSet { walk(::add) }.size }

    context(PuzzleInput)
    suspend fun part2() = visitWorld {
        buildSet {
            walk(visit = ::add)
            remove(start)
        }.parMap { withObstruction(it).walk() }.count { it }
    }

    inline fun <R> PuzzleInput.visitWorld(visit: World.() -> R): R {
        val start = run {
            lines.forEachIndexed { y, s ->
                val x = s.indexOf('^')
                if (x != -1) return@run Loc(x, y)
            }
            error("missing start")
        }

        return object : World {
            override val start = start
            override fun obstruction(loc: Loc) = lines[loc.y][loc.x] == '#'
        }.visit()
    }

    interface World {
        val start: Loc
        fun obstruction(loc: Loc): Boolean

        companion object {
            operator fun invoke(start: Loc, obstruction: (Loc) -> Boolean) = object : World {
                override val start = start
                override fun obstruction(loc: Loc) = obstruction(loc)
            }
        }
    }

    fun World.withObstruction(obstruction: Loc) =
        World(start) { obstruction == it || obstruction(it) }

    inline fun World.walk(visit: (Loc) -> Unit = { }): Boolean {
        var dir = Dir.U
        var pos = start

        val unseen = mutableSetOf<Int>()::add
        while (true) {
            val fwd = pos move dir
            when {
                !fwd.valid -> return false // walked off the world
                obstruction(fwd) -> dir = when {
                    unseen(pos x dir) -> dir.turn()
                    else -> return true // looped
                }

                else -> pos = fwd.also(visit)
            }
        }
    }

    enum class Dir {
        U, R, D, L;

        fun turn() = entries[ordinal.inc() % 4]
    }

    @JvmInline
    value class Loc(val raw: Int) {
        // offset x and y by 1 to detect when we go below a valid value without impacting the rest of the bits
        constructor(x: Int, y: Int)
            : this(x.inc() and 0xFF shl 8 or (y.inc() and 0xFF))

        inline val valid get() = raw and 0xFF00 in yMin..yMax && raw and 0xFF in 1..xMax
        inline val x get() = (raw shr 8 and 0xFF).dec()
        inline val y get() = (raw and 0xFF).dec()

        infix fun move(dir: Dir) = when (dir) {
            Dir.U -> -1
            Dir.R -> +0x100
            Dir.D -> +1
            Dir.L -> -0x100
        }.let { Loc(it + raw) }

        companion object {
            const val xMin = 1
            const val xMax = 130 // world size
            const val yMin = 1 shl 8
            const val yMax = xMax shl 8
        }

        override fun toString() = "($x,$y)"
    }

    infix fun Loc.x(dir: Dir) = raw shl 2 or dir.ordinal
}

fun main() {
    InputProvider.raw(
        """
        ....#.....
        .........#
        ..........
        ..#.......
        .......#..
        ..........
        .#..^.....
        ........#.
        #.........
        ......#...
    """.trimIndent()
    ).let {
        queryDay(6)//.solveAll();queryDay(100)
            .checkAll(5409, 2022)
//            .warmup(5.seconds)
            .warmup(sigma = 2.0)
            .solveAll(30)
    }
}
