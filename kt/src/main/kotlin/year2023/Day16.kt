package year2023

import aok.PuzDSL
import aoksp.AoKSolution
import year2023.Day16.Direction.*
import kotlin.experimental.and
import kotlin.experimental.or

fun main() = solveDay(16)

@AoKSolution
object Day16 : PuzDSL({
    fun List<String>.tile(beam: Beam) = getOrNull(beam.y)?.getOrNull(beam.x)

    fun List<String>.energize(start: Beam = Beam(0, 0, Right)): Int = with(ByteArray(size * first().length)) {
        fun visited(beam: Beam) = (beam.x * this@energize.size + beam.y).let { idx ->
            if (idx in indices) {
                val cur = this[idx]
                val bit = 1.shl(beam.d.ordinal).toByte()
                (cur and bit == bit).also { this[idx] = cur or bit }
            } else true
        }

        val pending = ArrayDeque(listOf(start))
        while (pending.isNotEmpty()) {
            var beam = pending.removeFirst()
            while (true) {
                val tile = tile(beam) ?: break
                if (visited(beam)) break
                beam = if (tile == '.') beam.move()
                else beam.handle(tile).also { (_, split) ->
                    split?.let { pending += it }
                }.first
            }
        }
        count { it > 0 }
    }

    part1 {
        lines.energize()
    }

    part2 {
        sequence {
            val lastY = lines.lastIndex
            val lastX = lines.first().lastIndex
            for (y in 0..lastY) {
                yield(Beam(0, y, Right))
                yield(Beam(lastX, y, Left))
            }
            for (x in 0..lastX) {
                yield(Beam(x, 0, Down))
                yield(Beam(x, lastY, Up))
            }
        }.maxOf(lines::energize)
    }
}) {
    enum class Direction {
        Up, Right, Down, Left;

        val isVertical by lazy { this == Up || this == Down }
        val turnLeft by lazy { entries[ordinal.dec().mod(entries.size)] }
        val turnRight by lazy { entries[ordinal.inc().mod(entries.size)] }
    }

    data class Beam(val x: Int, val y: Int, val d: Direction) {
        fun move() = when (d) {
            Up -> copy(y = y - 1)
            Down -> copy(y = y + 1)
            Left -> copy(x = x - 1)
            Right -> copy(x = x + 1)
        }

        fun handle(tile: Char) = when (tile to d.isVertical) {
            '|' to false, '-' to true -> copy(d = d.turnLeft).move() to copy(d = d.turnRight).move()
            '/' to true, '\\' to false -> copy(d = d.turnRight).move() to null
            '/' to false, '\\' to true -> copy(d = d.turnLeft).move() to null
            else -> move() to null
        }
    }
}
