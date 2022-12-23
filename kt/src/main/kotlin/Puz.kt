import aok.Puz
import aok.PuzKey
import aok.PuzzleInput
import year2022.AoKYear2022
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.TimedValue
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

// manually register sealed roots here (can't cross package boundaries)
private val years: List<KClass<out Puz<*, *>>> = listOf(AoKYear2022::class)

sealed interface Warmup {
    context(PuzzleInput)
    fun run(year: Int, day: Int, puzzles: List<Puz<*, *>>)

    class Iterations(val warmupIterations: Int) : Warmup {
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

    class DurationTotal(val d: Duration) : Warmup {
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

    class DurationPerSolution(val d: Duration) : Warmup {
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
}

private fun <T : Any> sealedObjects(kClass: KClass<out T>): Iterable<T> =
    (kClass.objectInstance?.let { listOf(it) } ?: emptyList()) + kClass.sealedSubclasses
        .flatMap(::sealedObjects)

fun solveAll(warmupIterations: Int = 0, runIterations: Int = 1, predicate: PuzKey.() -> Boolean = { true }) =
    with(InputScopeProvider) { queryPuzzles(predicate).solveAll(warmupIterations, runIterations) }

fun Iterable<Puz<*, *>>.solveAll(warmupIterations: Int = 0, runIterations: Int = 1) =
    with(InputScopeProvider) { solveAll(warmupIterations, runIterations) }

@JvmName("solveAllReified")
inline fun <reified T : Puz<*, *>> solveAll(
    warmupIterations: Int = 0,
    runIterations: Int = 1,
) = getAll<T, _, _>().solveAll(warmupIterations = warmupIterations, runIterations = runIterations)


context(InputScopeProvider)
fun Iterable<Puz<*, *>>.warmup(warmup: Warmup) = apply {
    groupBy { it.year to it.day }.forEach { (year, day), puzzles ->
        with(forPuzzle(year, day)) {
            warmup.run(year, day, puzzles)
        }
    }
}

context(InputScopeProvider)
fun Iterable<Puz<*, *>>.warmup(iterations: Int) = warmup(Warmup.Iterations(iterations))

context(InputScopeProvider)
fun Iterable<Puz<*, *>>.warmup(duration: Duration) = warmup(Warmup.DurationTotal(duration))

context(InputScopeProvider)
fun Iterable<Puz<*, *>>.warmupEach(duration: Duration) = warmup(Warmup.DurationPerSolution(duration))

context(InputScopeProvider)
fun Iterable<Puz<*, *>>.solveAll(warmupIterations: Int, runIterations: Int = 1) =
    warmup(warmupIterations).solveAll(runIterations)

context(InputScopeProvider)
fun Iterable<Puz<*, *>>.solveAll(runIterations: Int = 1) =
    groupBy { it.year to it.day }.forEach { (year, day), puzzles ->
        with(forPuzzle(year, day)) {
            fun runPart(part: Puz<*, *>.() -> Any?) {
                val results = puzzles.map { puz ->
                    puz.variant to runCatching {
                        measureTimedValue {
                            repeat(runIterations - 1) { puz.part() }
                            puz.part()
                        }.let { it.copy(duration = it.duration / runIterations) }
                    }.getOrElse {
                        TimedValue(
                            if (it is NotImplementedError) it else it.stackTraceToString(),
                            Duration.INFINITE
                        )
                    }
                }.sortedBy { (_, it) -> it.duration }
                results.forEach { (variant, it) ->
                    val result = it.value.toString().let {
                        if ('\n' in it) "\n" + it.lines().joinToString("\n") { line -> "\t\t$line" }
                        else it
                    }
                    println("\t $variant took ${it.duration}: $result")
                }
            }

            println("year $year day $day part 1")
            runPart { part1() }
            println("year $year day $day part 2")
            runPart { part2() }
        }
        println()
    }

fun main(): Unit = queryPuzzles().solveAll()
fun queryPuzzles(predicate: PuzKey.() -> Boolean = { true }) = years.flatMap(::sealedObjects).filter(predicate)
    .sortedWith(compareBy(PuzKey::year, PuzKey::day, PuzKey::variant))

inline fun <reified T : Puz<P1, P2>, P1, P2> getAll() = getAll(T::class)
fun <T : Puz<P1, P2>, P1, P2> getAll(vararg kClasses: KClass<out T>) = kClasses.flatMap(::sealedObjects)