package aok

import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.TimeSource
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

sealed interface Warmup {
    context(PuzzleInput)
    fun run(year: Int, day: Int, puzzles: List<Puz<*, *>>)

    private data object None : Warmup {
        context(PuzzleInput) override fun run(year: Int, day: Int, puzzles: List<Puz<*, *>>) = Unit
    }

    private class Auto(val history: Int = 5, val sigma: Double = 2.00) : Warmup {
        context(PuzzleInput) override fun run(year: Int, day: Int, puzzles: List<Puz<*, *>>) {
            val alive = puzzles.withIndex().toMutableList()
            val timings = puzzles.map { Array(history) { Duration.INFINITE } }
            val totals = Array(puzzles.size) { Duration.ZERO }
            var i = 0
            val total = measureTime {
                while (alive.isNotEmpty()) {
                    alive.listIterator().run {
                        val (idx, puz) = next()
                        val t = timings[idx]
                        val (success, duration) = measureTimedValue { puz.run() }
                        t[i % t.size] = duration
                        totals[idx] += duration
                        if (!success) remove()
                        else if (acceptable(t)) {
                            println("year $year day $day ${puz.variant} warmup ($i iterations) took ${totals[idx]}")
                            remove()
                        }
                    }
                    i++
                }
            }
            println("finished warmup after $total")
        }

        fun acceptable(t: Array<Duration>): Boolean {
            val numbers = t.map { it.inWholeMicroseconds }
            val mean = numbers.average()
            val variance = numbers.map { (it - mean).pow(2) }.average()
            val sigma = sqrt(variance).microseconds * sigma

            val avgDur = mean.microseconds
            val range = (avgDur - sigma)..(avgDur + sigma)
            return t.all { it in range }
        }
    }

    private class Iterations(val warmupIterations: Int) : Warmup {
        context(PuzzleInput) override fun run(year: Int, day: Int, puzzles: List<Puz<*, *>>) {
            if (warmupIterations > 0) {
                println("Warming up ${puzzles.size} puzzles $warmupIterations times for year $year day $day...")
                val alive = puzzles.toMutableList()
                measureTime {
                    repeat(warmupIterations) {
                        if (alive.isEmpty()) return@measureTime
                        alive.listIterator().run {
                            while (hasNext()) {
                                if (!next().run()) remove()
                            }
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
            val alive = puzzles.toMutableList()
            while (start.elapsedNow() < d && alive.isNotEmpty()) {
                alive.listIterator().run {
                    while (hasNext()) {
                        if (!next().run()) remove()
                    }
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
                    if (!it.run()) break
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
        fun auto(history: Int = 7, sigma: Double = 2.00): Warmup = Auto(history, sigma)
        val none: Warmup = None

        private fun runPart(year: Int, day: Int, part: Int, block: () -> Unit) = runCatching { block() }
            .getOrElse {
                if (it is NotImplementedError) Unit
                else {
                    println("⚠️ year $year day $day part $part failed in warmup: $it")
                    null
                }
            }

        context(PuzzleInput)
        private fun Puz<*, *>.run(): Boolean {
            runPart(year, day, 1) { part1() } ?: return false
            runPart(year, day, 2) { part2() } ?: return false
            return true
        }
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
fun Iterable<Puz<*, *>>.warmup(sigma: Double = 1.5, window: Int = 10) = warmup(aok.Warmup.auto(window, sigma))

context(InputProvider)
fun Iterable<Puz<*, *>>.warmup(iterations: Int) = warmup(aok.Warmup.iterations(iterations))

context(InputProvider)
fun Iterable<Puz<*, *>>.warmup(duration: Duration) = warmup(aok.Warmup.allFor(duration))

context(InputProvider)
fun Iterable<Puz<*, *>>.warmupEach(eachFor: Duration) = warmup(aok.Warmup.eachFor(eachFor))

fun Iterable<Puz<*, *>>.warmup(iterations: Int) = with(InputProvider) { warmup(iterations) }
fun Iterable<Puz<*, *>>.warmup(duration: Duration) = with(InputProvider) { warmup(duration) }
fun Iterable<Puz<*, *>>.warmup(sigma: Double = 1.4, window: Int = 20) =with(InputProvider) { warmup(sigma, window) }

fun Iterable<Puz<*, *>>.warmupEach(duration: Duration) = with(InputProvider) { warmupEach(duration) }
