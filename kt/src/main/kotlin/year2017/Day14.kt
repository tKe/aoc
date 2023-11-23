package year2017

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day14 : PuzDSL({
    fun String.gridHash() = sequence {
        repeat(128) { y ->
            yield(
                "${this@gridHash}-$y".knotHash().flatMap { (7 downTo 0).map { bit -> it.toInt().shr(bit).and(1) == 1 } }
                    .toBooleanArray()
            )
        }
    }.toList()

    part1 {
        input.gridHash().sumOf { it.count { b -> b } }
    }

    fun Int.neighbours(width: Int) = sequence {
        val col = rem(width)
        if (col != 0) yield(dec())
        if (col != (width - 1)) yield(inc())
        yield(plus(width))
        yield(minus(width))
    }

    part2 {
        val grid = input.gridHash().flatMap { it.asIterable() }.toBooleanArray()
        val queue = ArrayDeque<Int>()
        var count = 0
        while (grid.any { it }) {
            queue += grid.indexOfFirst { it }
            while (queue.isNotEmpty()) {
                val node = queue.removeFirst()
                grid[node] = false
                for (neighbour in node.neighbours(128).filter(grid.indices::contains)) {
                    if (grid.getOrElse(neighbour) { false }) queue += neighbour
                }
            }
            count++
        }
        count
    }
})

@AoKSolution
object Day14SetPairs : PuzDSL({
    data class IntPair(val x: Int, val y: Int)

    fun Byte.bits() = sequence {
        repeat(8) {
            if(this@bits.toInt().and(1.shl(it)) != 0) yield(it)
        }
    }
    fun String.gridHash() = buildSet {
        repeat(128) { y ->
            "${this@gridHash}-$y".knotHash()
                .forEachIndexed { idx, byte ->
                    addAll(byte.bits().map { IntPair(it + idx * 8, y) })
                }
        }
    }

    part1 {
        input.gridHash().size
    }

    fun IntPair.neighbours() = sequence {
        yield(IntPair(x + 1, y))
        yield(IntPair(x - 1, y))
        yield(IntPair(x, y + 1))
        yield(IntPair(x, y - 1))
    }

    part2 {
        val grid = input.gridHash().toMutableSet()
        val queue = ArrayDeque<IntPair>()
        var count = 0
        while (grid.isNotEmpty()) {
            queue += grid.first()
            while (queue.isNotEmpty()) {
                val node = queue.removeFirst()
                grid -= node
                queue += node.neighbours().filter(grid::remove)
            }
            count++
        }
        count
    }
})

private fun String.knotHash(): ByteArray { // taken from y2017d10
    fun <T> List<T>.rotateBy(n: Int): List<T> = object : AbstractList<T>() {
        override val size = this@rotateBy.size
        override fun get(index: Int) = this@rotateBy[(index + n).mod(size)]
    }

    fun <T> List<T>.twist(start: Int, length: Int) =
        with(rotateBy(start)) { (subList(0, length).asReversed() + drop(length)) }.rotateBy(-start)

    fun <T> List<T>.knot(lengths: List<Int>) = lengths
        .foldIndexed(this to 0) { skip, (list, ofs), len ->
            list.twist(ofs, len) to (ofs + len + skip)
        }
        .first

    operator fun <T> List<T>.times(n: Int) = List(n) { this }.flatten()
    return List(256) { it }
        .knot((toByteArray().map(Byte::toInt) + listOf(17, 31, 73, 47, 23)) * 64)
        .chunked(16) { c -> c.reduce(Int::xor).toByte() }
        .toByteArray()
}


fun main(): Unit = solveDay(
    14,
)
