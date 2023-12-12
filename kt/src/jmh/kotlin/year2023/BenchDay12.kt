package year2023

import aok.InputProvider
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
open class BenchDay12 {
    private val input = InputProvider.forPuzzle(2023, 12)

    @Benchmark
    fun normalRecursionPart1() = runBlocking { with(input) { Day12.part1() } }
    @Benchmark
    fun normalRecursionPart2() = runBlocking { with(input) { Day12.part2() } }

    @Benchmark
    fun deepRecursivePart1() = runBlocking { with(input) { Day12Recurse.part1() } }
    @Benchmark
    fun deepRecursivePart2() = runBlocking { with(input) { Day12Recurse.part2() } }
}
