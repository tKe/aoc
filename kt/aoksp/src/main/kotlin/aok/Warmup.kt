package aok

import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.measureTime

sealed interface Warmup {
    context(PuzzleInput)
    fun run(year: Int, day: Int, puzzles: List<Puz<*, *>>)

    private object None: Warmup {
        context(PuzzleInput) override fun run(year: Int, day: Int, puzzles: List<Puz<*, *>>) = Unit
    }

    private class Iterations(val warmupIterations: Int) : Warmup {
        context(PuzzleInput) override fun run(year: Int, day: Int, puzzles: List<Puz<*, *>>) {
            if (warmupIterations > 0) {
                println("Warming up ${puzzles.size} puzzles $warmupIterations times for year $year day $day...")
                measureTime {
                    repeat(warmupIterations) {
                        puzzles.forEach {
                            runCatching { it.part1() }
                            runCatching { it.part2() }
                        }
                    }
                }.also { println("year $year day $day warmup ($warmupIterations iterations) took $it") }
            }
        }
    }

    private class DurationTotal(val d: Duration) : Warmup {
        context(PuzzleInput) override fun run(year: Int, day: Int, puzzles: List<Puz<*, *>>) {
            val start = TimeSource.Monotonic.markNow()
            println("Warming up ${puzzles.size} puzzles over $d for year $year day $day...")
            var count = 0
            while (start.elapsedNow() < d) {
                puzzles.forEach {
                    runCatching { it.part1() }
                    runCatching { it.part2() }
                }
                count++
            }
            println("Warmup finished after ${start.elapsedNow()} with $count iterations")
        }
    }

    private class DurationPerSolution(val d: Duration) : Warmup {
        context(PuzzleInput) override fun run(year: Int, day: Int, puzzles: List<Puz<*, *>>) {
            println("Warming up ${puzzles.size} puzzles for $d each for year $year day $day...")
            puzzles.forEach {
                val start = TimeSource.Monotonic.markNow()
                var count = 0
                while (start.elapsedNow() < d) {
                    runCatching { it.part1() }
                    runCatching { it.part2() }
                    count++
                }
                println("\t${it.variant} warmed up with $count iterations")
            }
        }
    }

    companion object {
        fun eachFor(duration: Duration): Warmup = DurationPerSolution(duration)
        fun allFor(duration: Duration): Warmup = DurationTotal(duration)
        fun iterations(n: Int): Warmup = Iterations(n)
        val none: Warmup = None
    }
}

context(InputProvider)
fun Iterable<Puz<*, *>>.warmup(warmup: Warmup) = apply {
    groupBy { it.year to it.day }.forEach { (year, day), puzzles ->
        with(forPuzzle(year, day)) {
            warmup.run(year, day, puzzles)
        }
    }
}

context(InputProvider)
fun Iterable<Puz<*, *>>.warmup(iterations: Int) = warmup(aok.Warmup.iterations(iterations))

context(InputProvider)
fun Iterable<Puz<*, *>>.warmup(duration: Duration) = warmup(aok.Warmup.allFor(duration))

context(InputProvider)
fun Iterable<Puz<*, *>>.warmupEach(duration: Duration) = warmup(aok.Warmup.eachFor(duration))

fun Iterable<Puz<*, *>>.warmup(iterations: Int = 1) = with(InputProvider) { warmup(iterations) }
fun Iterable<Puz<*, *>>.warmup(duration: Duration) = with(InputProvider) { warmup(duration) }
fun Iterable<Puz<*, *>>.warmupEach(duration: Duration) = with(InputProvider) { warmupEach(duration) }
