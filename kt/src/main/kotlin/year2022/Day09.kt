@file:OptIn(ExperimentalStdlibApi::class)

package year2022

import aok.PuzDSL
import aok.PuzzleInput
import aok.Warmup
import aoksp.AoKSolution
import java.util.BitSet
import kotlin.math.sign
import kotlin.time.Duration.Companion.seconds

fun main() = solveDay(day = 9, warmup = Warmup.eachFor(5.seconds), runs = 3)

@AoKSolution
object Day09ImmutableRope : PuzDSL({
    data class Point(val x: Int = 0, val y: Int = 0) {
        operator fun minus(other: Point) = Point(x - other.x, y - other.y)
        operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    }

    data class Rope(val head: Point = Point(), val tail: Rope? = null) : Iterable<Point> {
        override fun iterator() = generateSequence(this, Rope::tail).map(Rope::head).iterator()

        private fun follow(leadHead: Point) = (leadHead - head).let { (x, y) ->
            if (x * x + y * y < 4) this
            else moveHead(Point(x.sign, y.sign))
        }

        fun moveHead(offset: Point): Rope =
            (head + offset).let { copy(head = it, tail = tail?.follow(it)) }
    }

    fun Rope.moveHead(direction: Char): Rope = moveHead(
        when (direction) {
            'D' -> Point(y = 1)
            'U' -> Point(y = -1)
            'L' -> Point(x = -1)
            'R' -> Point(x = 1)
            else -> error("wtf? $direction??")
        }
    )

    tailrec fun Rope.extend(size: Int): Rope =
        if (size <= 0) this
        else Rope(tail = this).extend(size - 1)

    fun ropeOfLength(size: Int): Rope {
        require(size > 0)
        return Rope(tail = null).extend(size - 1)
    }

    fun PuzzleInput.solve(ropeSize: Int) = lineSeq
        .flatMap { line -> sequence { repeat(line.substring(2).toInt()) { yield(line[0]) } } }
        .runningFold(ropeOfLength(ropeSize), Rope::moveHead)
        .map(Rope::last)
        .toSet().size

    part1 { solve(2) }
    part2 { solve(10) }
})

@AoKSolution
object Day09Arrays : PuzDSL({
    fun Int.zigZag() = shr(15) xor shl(1)
    infix fun Int.pairing(other: Int) = other + (plus(other) * plus(other + 1) shr 1)

    fun PuzzleInput.solve(ropeSize: Int): Int {
        val x = IntArray(ropeSize)
        val y = IntArray(ropeSize)
        val visited = BitSet()
        lineSeq.forEach { line ->
            val direction = line[0]
            repeat(line.substring(2).toInt()) {
                // move head
                when (direction) {
                    'D' -> y[0]++
                    'U' -> y[0]--
                    'L' -> x[0]--
                    'R' -> x[0]++
                }

                // follow
                for (ofs in 1..<ropeSize) {
                    val dy = y[ofs - 1] - y[ofs]
                    val dx = x[ofs - 1] - x[ofs]
                    if (dx * dx + dy * dy < 4) break // no movement
                    else {
                        y[ofs] += dy.sign
                        x[ofs] += dx.sign
                    }
                }

                // tail location
                val tx = x[x.lastIndex]
                val ty = y[y.lastIndex]
                visited.set(tx.zigZag() pairing ty.zigZag())
            }
        }
        return visited.cardinality()
    }

    part1 { solve(2) }
    part2 { solve(10) }
})

@AoKSolution
object Day09Sequences : PuzDSL({
    fun PuzzleInput.headSeq() = sequence {
        var x = 0
        var y = 0
        for (line in lines) {
            val d = line[0]
            repeat(line.substring(2).toInt()) {
                when (d) {
                    'D' -> yield(x to ++y)
                    'U' -> yield(x to --y)
                    'L' -> yield(--x to y)
                    'R' -> yield(++x to y)
                }
            }
        }
    }

    fun Sequence<Pair<Int, Int>>.follow() = sequence {
        var x = 0
        var y = 0
        for ((hx, hy) in this@follow) {
            val dy = hy - y
            val dx = hx - x
            if (dx * dx + dy * dy >= 4) {
                x += dx.sign
                y += dy.sign
                yield(x to y)
            }
        }
    }

    fun PuzzleInput.solve(ropeSize: Int) =
        (1..<ropeSize).fold(headSeq()) { it, _ -> it.follow() }
            .distinct().count() + 1 // remember the start

    part1 { solve(2) }
    part2 { solve(10) }
})
