@file:OptIn(ExperimentalCoroutinesApi::class)

package year2017

import aok.PuzDSL
import aok.PuzzleInput
import aok.Warmup
import aoksp.AoKSolution
import kotlinx.coroutines.ExperimentalCoroutinesApi
import year2017.Day21.INITIAL
import year2017.Day21.enhance
import year2017.Day21.equivalents

@AoKSolution
object Day21 : PuzDSL({
    fun PuzzleInput.loadMappings() = buildMap {
        lines.forEach {
            val (from, to) = it.split(" => ").map(GridInt::invoke)
            from.equivalents()
                .map(GridInt::invoke)
                .distinct()
                .forEach { put(it, to) }
        }
    }

    fun Map<GridInt, GridInt>.simulate(initialGrid: Grid<Boolean> = GridInt(INITIAL)) =
        generateSequence(initialGrid) { g -> g.enhance { getValue(GridInt(it)) } }

    part1 {
        loadMappings().simulate().elementAt(5).count(true::equals)
    }
    part2 {
        loadMappings().simulate().elementAt(18).count(true::equals)
    }
}) {
    const val INITIAL = ".#./..#/###"

    @JvmInline
    value class GridInt(val v: Int) : Grid<Boolean> {
        constructor(size: Int, cells: Iterable<Boolean>) : this(
            size.shl(SIZE_SHIFT).or(cells.fold(0) { acc, it -> acc.shl(1).or(if (it) 1 else 0) })
        )

        init {
            require(size in 0..4)
        }

        override val size get() = v.ushr(SIZE_SHIFT)
        override fun get(x: Int, y: Int) = v.shr((size * size - 1) - (y * size + x)).and(1) == 1

        override fun toString() =
            chunked(size) { it.joinToString("") { if (it) "#" else "." } }.joinToString("/") + "($v)"

        companion object {
            private const val SIZE_SHIFT = 16
            operator fun invoke(other: Grid<Boolean>) =
                if (other is GridInt) other else GridInt(other.size, other)

            operator fun invoke(repr: String) = repr.split("/").let { r ->
                require(r.all { it.length == r.size })
                GridInt(r.size, r.flatMap { it.map('#'::equals) })
            }
        }
    }

    private fun <T> Grid<T>.flip(flipX: Boolean = false, flipY: Boolean = false, swapXY: Boolean = false) =
        Grid(size) { x, y ->
            val nx = if (flipX) size - x - 1 else x
            val ny = if (flipY) size - y - 1 else y
            if (swapXY) get(ny, nx) else get(nx, ny)
        }

    fun <T> Grid<T>.equivalents() = (0..0b111).asSequence().map {
        flip(it.and(1) == 1, it.and(2) == 2, it.and(4) == 4)
    }

    interface Grid<T> : Iterable<T> {
        val size: Int
        operator fun get(x: Int, y: Int): T
        fun <R : Any> map(transform: (T) -> R): Grid<R> = asIterable().map(transform).asGrid(size)

        override fun iterator() = iterator {
            repeat(size) { y ->
                repeat(size) { x ->
                    yield(get(x, y))
                }
            }
        }

        fun subgrid(ox: Int, oy: Int, size: Int): Grid<T> = Grid(size) { x, y -> get(ox + x, oy + y) }

        companion object {
            operator fun <T> invoke(size: Int, get: (x: Int, y: Int) -> T) = object : Grid<T> {
                override val size = size
                override fun get(x: Int, y: Int): T {
                    require(x in 0..<size && y in 0..<size)
                    return get(x, y)
                }
            }
        }
    }

    fun <T> List<T>.asGrid(stride: Int): Grid<T> = Grid(stride) { x, y -> get(y * stride + x) }

    private fun <T> Grid<Grid<T>>.flatten(): Grid<T> {
        val subgridSize = distinctBy { it.size }.singleOrNull()?.size ?: error("not all subgrids are the same size!")
        return Grid(size * subgridSize) { x, y ->
            get(x / subgridSize, y / subgridSize)[x % subgridSize, y % subgridSize]
        }
    }

    fun <T> Grid<T>.split(): Grid<Grid<T>> = when {
        size % 2 == 0 -> split(2)
        size % 3 == 0 -> split(3)
        else -> TODO("unsupported split size")
    }
    fun <T> Grid<T>.split(n: Int) = Grid(size / n) { x, y -> subgrid(x * n, y * n, n) }

    fun Grid<Boolean>.enhance(enhancement: (Grid<Boolean>) -> Grid<Boolean>) =
        split().map(enhancement).flatten()
}

fun main(): Unit = solveDay(21)
