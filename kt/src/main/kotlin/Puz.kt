import year2022.Day07JimFS
import year2022.Puz22
import year2022.AoKYear2022
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.TimedValue
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

typealias PuzKey = aok.PuzKey
typealias Puz<A, B> = aok.Puz<A, B>

// manually register sealed roots here (can't cross package boundaries)
private val years = listOf(Puz22::class, AoKYear2022::class)

abstract class PuzYear<P1, P2>(final override val year: Int, final override val day: Int, variant: String?) :
    Puz<P1, P2> {
    final override val variant: String = variant ?: defaultVariant
}

abstract class PuzYearDSL<P1, P2>(year: Int, day: Int, variant: String? = null, def: PuzzleDefinition<P1, P2>) :
    PuzYear<P1, P2>(year, day, variant) {
    private val solutions by def
    context(InputScope) override fun part1(): P1 = solutions.first()
    context(InputScope) override fun part2(): P2 = solutions.second()
}

private val Puz<*, *>.defaultVariant
    get() = this::class.simpleName
        ?.removePrefix("Day%02d".format(day))
        ?.takeUnless(String::isBlank)
        ?: "Default"

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
fun Iterable<Puz<*, *>>.solveAll(warmupIterations: Int = 0, runIterations: Int = 1) =
    groupBy { it.year to it.day }.forEach { (year, day), puzzles ->
        with(forPuzzle(year, day, "input.txt")) {
            if (warmupIterations > 0) {
                measureTime {
                    repeat(warmupIterations) {
                        puzzles.forEach {
                            runCatching { it.part1() }
                            runCatching { it.part2() }
                        }
                    }
                }.also { println("year $year day $day warmup ($warmupIterations iterations) took $it") }
            }

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

fun main() = solveAll(warmupIterations = 500, runIterations = 3) { this != Day07JimFS }
fun queryPuzzles(predicate: PuzKey.() -> Boolean = { true }) = years.flatMap(::sealedObjects).filter(predicate)
inline fun <reified T : Puz<P1, P2>, P1, P2> getAll() = getAll(T::class)
fun <T : Puz<P1, P2>, P1, P2> getAll(vararg kClasses: KClass<out T>) = kClasses.flatMap(::sealedObjects)