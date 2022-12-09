import Puz.Companion.query
import arrow.core.Either
import year2022.Day07JimFS
import year2022.Puz22
import kotlin.reflect.KClass
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

sealed interface PuzKey {
    val year: Int
    val day: Int
    val variant: String
}

interface Puz<out P1, out P2> : PuzKey {
    context(InputScope) fun part1(): P1
    context(InputScope) fun part2(): P2

    companion object {
        fun query(predicate: (PuzKey) -> Boolean = { true }) = years.flatMap(::sealedObjects).filter(predicate)
        inline fun <reified T : Puz<P1, P2>, P1, P2> getAll() = getAll(T::class)
        fun <T : Puz<P1, P2>, P1, P2> getAll(vararg kClasses: KClass<out T>) = kClasses.flatMap(::sealedObjects)
    }
}

// manually register sealed roots here (can't cross package boundaries)
private val years = listOf(Puz22::class)

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

context(InputScopeProvider)
fun <P1, P2> Iterable<Puz<P1, P2>>.solveAll(iterations: Int = 1) = groupBy { it.year to it.day }
    .forEach { (year, day), puzzles ->
        println("Solving Year $year Day $day")
        Either.catch { forPuzzle(year, day) }
            .tapLeft(System.err::println)
            .map { input ->
                with(input) {
                    puzzles.forEach { it.solve(iterations) }
                }
            }
    }

context(InputScope)
@JvmName("solveWithInput")
fun <P1, P2> Puz<P1, P2>.solve(iterations: Int = 100) {
    fun <T> solveAndLog(part: String, block: () -> T) {
        runCatching {
            measureTimedValue {
                repeat(iterations - 1) { block() }
                block()
            }
        }.fold(
            {
                val formatted = it.value.toString().lines().run {
                    if (size <= 1) first()
                    else "\n" + joinToString("\n") { line -> "    $line" }
                }
                "  $variant $part (took ${it.duration / iterations} over $iterations runs): $formatted"
            },
            {
                "  $variant $part failed: $it"
            }
        ).also(::println)
    }
    solveAndLog("part1") { part1() }
    solveAndLog("part2") { part2() }
}

fun solveAll(
    vararg remappings: Pair<String, String>,
    warmupIterations: Int = 0,
    runIterations: Int = 1,
    predicate: (PuzKey) -> Boolean = { true }
) = with(InputScopeProvider.mapping(*remappings)) {
    query(predicate).solveAll(warmupIterations, runIterations)
}

@JvmName("solveAllReified")
inline fun <reified T : Puz<*, *>> solveAll(
    vararg remappings: Pair<String, String>,
    warmupIterations: Int = 0,
    runIterations: Int = 1,
) = solveAll(*remappings, warmupIterations = warmupIterations, runIterations = runIterations) { it is T }

context(InputScopeProvider)
fun Iterable<Puz<*, *>>.solveAll(warmupIterations: Int = 0, runIterations: Int = 1) =
    groupBy { it.year to it.day }.forEach { (year, day), puzzles ->
        with(forPuzzle(year, day)) {
            if (warmupIterations > 0) {
                measureTime {
                    repeat(warmupIterations) {
                        puzzles.forEach {
                            it.part1()
                            it.part2()
                        }
                    }
                }.also { println("year $year day $day warmup ($warmupIterations iterations) took $it") }
            }

            fun runPart(part: Puz<*, *>.() -> Any?) {
                val results = puzzles.map { puz ->
                    puz.variant to measureTimedValue {
                        repeat(runIterations - 1) { puz.part() }
                        puz.part()
                    }.let { it.copy(duration = it.duration / runIterations) }
                }.sortedBy { (_, it) -> it.duration }
                results.forEach { (variant, it) -> println("\t $variant took ${it.duration}: ${it.value}") }
            }

            println("year $year day $day part 1")
            runPart { part1() }
            println("year $year day $day part 2")
            runPart { part2() }
        }
        println()
    }

fun main() = solveAll(warmupIterations = 3000, runIterations = 5) { it != Day07JimFS }
