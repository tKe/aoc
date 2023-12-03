package year2023

import aok.PuzDSL
import aoksp.AoKSolution
import kotlin.math.max
import kotlin.math.min

fun main() = solveDay(
    3,
)

@AoKSolution
object Day03 : PuzDSL({
    part1 {
        lines.mapIndexed { y, line ->
            "\\d+".toRegex().findAll(line).filter { m ->
                val sr = max(m.range.first - 1, 0)..min(m.range.last + 1, line.lastIndex)
                (max(y-1, 0)..min(y+1, lines.lastIndex))
                    .map { lines[it].substring(sr) }
                    .any { it.any { c -> c != '.' && !c.isDigit() } }
            }.sumOf { it.value.toInt() }
        }.sum()
    }
    part2 {
        lines.mapIndexed { y, line ->
            line.mapIndexedNotNull { idx, c -> idx.takeIf{ c == '*' } }.sumOf { x ->
                (max(y - 1, 0)..min(y + 1, lines.lastIndex)).flatMap {
                    "\\d+".toRegex().findAll(lines[it]).filter { m ->
                        x in m.range || (x - 1) in m.range || (x + 1) in m.range
                    }.map { it.value.toInt() }
                }.takeIf { it.size == 2 }?.let { (a, b) -> a * b } ?: 0
            }
        }.sum()
    }
})