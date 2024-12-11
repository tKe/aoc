package year2024

import aok.PuzDSL
import aok.PuzzleInput
import aok.Warmup
import aoksp.AoKSolution
import utils.splitIntsNotNull
import kotlin.math.abs
import kotlin.math.absoluteValue

@AoKSolution
object Day01Initial : PuzDSL({
    fun <T> PuzzleInput.lists(block: (a: List<Int>, b: List<Int>) -> T) =
        input.splitIntsNotNull(" ", "\n").let { ints ->
            val a = List(ints.size / 2) { ints[it * 2] }
            val b = List(ints.size / 2) { ints[1 + it * 2] }
            block(a, b)
        }

    part1 {
        lists { a, b ->
            a.sorted().zip(b.sorted()) { ai, bi -> abs(ai - bi) }.sum()
        }
    }

    part2 {
        lists { a, b ->
            val counts = b.groupingBy { it }.eachCount()
            a.sumOf { it * counts.getOrDefault(it, 0) }
        }
    }
})

@AoKSolution
object Day01Imp : PuzDSL({
    fun <T> PuzzleInput.lists(block: (a: IntArray, b: IntArray) -> T) =
        lines.foldIndexed(IntArray(lines.size) to IntArray(lines.size)) { idx, acc, line ->
            acc.also { (a, b) ->
                a[idx] = line.substringBefore(' ').toInt()
                b[idx] = line.substringAfterLast(' ').toInt()
            }
        }.run { block(first, second) }

    part1 {
        lists { a, b ->
            a.sort()
            b.sort()
            a.zip(b) { ai, bi -> (ai - bi).absoluteValue }.sum()
        }
    }
    part2 {
        lists { a, b ->
            val counts = b.asList().groupingBy { it }.eachCount()
            a.sumOf { (counts[it] ?: 0) * it }
        }
    }
})

@AoKSolution
object Day01 : PuzDSL({
    part1 {
        val left = lines.map { it.substringBefore(' ').toInt() }.sorted()
        val right = lines.map { it.substringAfterLast(' ').toInt() }.sorted()
        left.zip(right) { a, b -> (a - b).absoluteValue }.sum()
    }
    part2 {
        val left = lines.map { it.substringBefore(' ') }
        val rightCounts = lines.groupingBy { it.substringAfterLast(' ') }.eachCount()
        left.sumOf { rightCounts[it]?.let { count -> it.toInt() * count } ?: 0 }
    }
})

fun main() = solveDay(1, warmup = Warmup.iterations(5000), runs = 50)
