package year2023

import aok.InputProvider
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Thread)
open class BenchDay09 {
    private val input = InputProvider.forPuzzle(2023, 9)

    @Benchmark
    fun mathsPart1() = runBlocking { with(input) { Day09Maths.part1() } }
    @Benchmark
    fun mathsPart2() = runBlocking { with(input) { Day09Maths.part2() } }
    @Benchmark
    fun arrayPart1() = runBlocking { with(input) { Day09Array.part1() } }
    @Benchmark
    fun arrayPart2() = runBlocking { with(input) { Day09Array.part2() } }
}
