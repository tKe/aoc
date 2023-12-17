package year2023

import aok.InputProvider
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
open class BenchDay17 {
    private val input = InputProvider.forPuzzle(2023, 17)

    @Benchmark
    fun part1() = runBlocking { with(input) { Day17.part1() } }

    @Benchmark
    fun part2() = runBlocking { with(input) { Day17.part2() } }

    @Benchmark
    fun intArrayPart1() = runBlocking { with(input) { Day17IntArray.part1() } }

    @Benchmark
    fun intArrayPart2() = runBlocking { with(input) { Day17IntArray.part2() } }
}
