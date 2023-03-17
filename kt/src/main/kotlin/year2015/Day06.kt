package year2015

import aok.PuzDSL
import aoksp.AoKSolution
import java.util.*

@AoKSolution
object Day06 : PuzDSL({

    part1 {
        val lights = BitSet(1000 * 1000)
        operator fun BitSet.get(action: String): (Int, Int) -> Unit = when {
            action.startsWith("toggle ") -> { a, b -> flip(a, b + 1) }
            action.startsWith("turn on ") -> { a, b -> set(a, b + 1) }
            else -> { a, b -> clear(a, b + 1) }
        }

        lines.forEach {
            val (x1, y1, x2, y2) = it.split(' ', ',').mapNotNull(String::toIntOrNull)
            val (sx, ex) = minOf(x1, x2) to maxOf(x1, x2)
            for (y in (y1..y2).map(1000::times)) lights[it](y + sx, y + ex)
        }

        lights.cardinality()
    }

    part2 {
        val lights = IntArray(1000 * 1000)
        operator fun IntArray.get(action: String): (Int, Int) -> Unit {
            val delta = when {
                action.startsWith("toggle ") -> 2
                action.startsWith("turn on ") -> 1
                else -> -1
            }
            return { a, b ->
                (a..b).forEach {
                    this[it] = maxOf(0, this[it] + delta)
                }
            }
        }

        lines.forEach {
            val (x1, y1, x2, y2) = it.split(' ', ',').mapNotNull(String::toIntOrNull)
            val (sx, ex) = minOf(x1, x2) to maxOf(x1, x2)
            for (y in (y1..y2).map(1000::times)) lights[it](y + sx, y + ex)
        }

        lights.sum()
    }
})

fun main() = solveDay(6)
