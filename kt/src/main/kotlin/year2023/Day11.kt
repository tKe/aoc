package year2023

import aok.PuzDSL
import aoksp.AoKSolution
import utils.combinations
import kotlin.math.absoluteValue

fun main() = solveDay(
    11
)

@AoKSolution
object Day11 : PuzDSL({
    data class Int2(val x: Int, val y: Int)
    data class Long2(val x: Long, val y: Long)

    val parser = parser {
        val emptyCols = lines.indices.toMutableSet()
        val emptyRows = mutableSetOf<Int>()
        val galaxies = lines.flatMapIndexed { y, s ->
            val galaxies = s.mapIndexedNotNull { x, c -> x.takeIf { c == '#' } }.toSet()
            emptyCols -= galaxies
            if (galaxies.isEmpty()) emptyRows += y
            galaxies.map { Int2(it, y) }.toSet()
        }
        Triple(galaxies, emptyCols.sorted(), emptyRows.sorted())
    }

    fun Iterable<Int2>.expand(cols: List<Int>, rows: List<Int>, by: Long = 1) = map { (x, y) ->
        Long2(x + by * cols.count { it < x }, y + by * rows.count { it < y })
    }

    part1(parser) { (galaxies, emptyCols, emptyRows) ->
        val expanded = galaxies.expand(emptyCols, emptyRows)
        expanded.combinations(2).sumOf { (a, b) ->
            (a.x - b.x).absoluteValue + (a.y - b.y).absoluteValue
        }
    }

    part2(parser) { (galaxies, emptyCols, emptyRows) ->
        val expanded = galaxies.expand(emptyCols, emptyRows, by=999_999)
        expanded.combinations(2).sumOf { (a, b) ->
            (a.x - b.x).absoluteValue + (a.y - b.y).absoluteValue
        }
    }
})