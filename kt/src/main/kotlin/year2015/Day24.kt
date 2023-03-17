package year2015

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day24 : PuzDSL({

    fun selectPackages(packages: List<Int>, groupSize: Int): Long {
        data class State(val selected: Selection, val available: Selection, val weight: Int, val qe: Long)

        val allPackages = packages.indices.fold(Selection(0), Selection::plus)
        val queue = ArrayDeque(listOf(State(Selection(0), allPackages, 0, 1)))
        val visited = mutableSetOf<Selection>()

        while (queue.isNotEmpty()) {
            val (selected, available, weight, qe) = queue.removeFirst()
            val remaining = groupSize - weight
            if (remaining == 0) return qe else {
                for (nextIndex in available) {
                    val nextPackage = packages[nextIndex]
                    val nextSelection = selected + nextIndex
                    if (visited.add(nextSelection)) {
                        queue += State(nextSelection, available - nextIndex, weight + nextPackage, qe * nextPackage)
                    }
                }
            }
        }
        return -1
    }

    part1 {
        val packages = lines.map(String::toInt).sortedDescending()
        selectPackages(packages, packages.sum() / 3)
    }

    part2 {
        val packages = lines.map(String::toInt).sortedDescending()
        selectPackages(packages, packages.sum() / 4)
    }
}) {
    @JvmInline
    value class Selection(private val long: Long) : Iterable<Int> {
        val size get() = long.countOneBits()
        operator fun plus(idx: Int) = Selection(long or (1L shl idx))
        operator fun minus(idx: Int) = Selection(long and (1L shl idx).inv())
        operator fun minus(other: Selection) = Selection(long and other.long.inv())
        operator fun contains(idx: Int) = long and (1L shl idx) != 0L
        override fun iterator() = iterator {
            var mask = 1L
            while (mask <= long) {
                if (mask and long != 0L) yield(mask.countTrailingZeroBits())
                mask = mask shl 1
            }
        }
    }
}


fun main() = solveDay(
    24,
)
