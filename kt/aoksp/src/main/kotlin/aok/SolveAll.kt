package aok

import kotlin.reflect.KFunction1
import kotlin.system.exitProcess
import kotlin.time.Duration
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

context(InputProvider)
fun Iterable<Puz<*, *>>.solveAll(runIterations: Int = 1) =
    sortedWith(compareBy<Puz<*, *>> { it.year }.thenBy { it.day })
        .groupBy { it.year to it.day }.forEach { (year, day), puzzles ->
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
                    val fastest = results.minOf { it.second.duration }
                    results.forEach { (variant, it) ->
                        val result = it.value.toResultString()
                        val multiplier =
                            if (it.duration == fastest) "👑" else "(${"%.2f".format(it.duration / fastest)}x)"
                        println("\t $variant took ${it.duration} $multiplier: $result")
                    }
                    results.filterNot { it.second.value is NotImplementedError? }
                        .distinctBy { it.second.value }.takeIf { it.size > 1 }?.let {
                            println("\t ⚠️WARNING⚠️ mismatched results")
                        }
                }

                println("year $year day $day part 1")
                runPart { part1() }
                println("year $year day $day part 2")
                runPart { part2() }
            }
            println()
        }

private fun Any?.repr() = when (this) {
    null -> "<null>"
    is Array<*> -> contentDeepToString()
    is BooleanArray -> contentToString()
    is ByteArray -> contentToString() // TODO: hex/base64?
    is ShortArray -> contentToString()
    is IntArray -> contentToString()
    is LongArray -> contentToString()
    is FloatArray -> contentToString()
    is DoubleArray -> contentToString()
    else -> toString()
}

private fun Any?.toResultString() = repr().let {
    if ('\n' in it) "\n" + it.lines().joinToString("\n") { line -> "\t\t$line" }
    else it
}

// default inputs
fun Iterable<Puz<*, *>>.solveAll(runIterations: Int = 1) = with(InputProvider) { solveAll(runIterations) }

private object NotChecked

context(InputProvider)
fun Iterable<Puz<*, *>>.checkAll(part1: Any? = NotChecked, part2: Any? = NotChecked, exit: Boolean = true) = also {
    var failures = false
    fun PuzzleInput.check(puz: Puz<*, *>, expected: Any? = NotChecked, part: KFunction1<PuzzleInput, Any?>) {
        if (expected != NotChecked) {
            val actual = runCatching { part(this) }.getOrElse { it }
            if (actual != expected) {
                failures = true
                System.err.println("⚠️ ${puz.year}-${puz.day}-${puz.variant} ${part.name} invalid - was $actual but expected $expected")
            }
        }
    }
    forEach { puz ->
        with(forPuzzle(puz.year, puz.day)) {
            check(puz, part1, puz::part1)
            check(puz, part2, puz::part2)
        }
        println("Checked ${puz.year}-${puz.day}-${puz.variant}")
    }
    if (failures && exit) {
        System.err.println("‼️ exiting due to failures detected")
        exitProcess(1)
    }
}

fun Iterable<Puz<*, *>>.checkAll(
    part1: Any? = NotChecked,
    part2: Any? = NotChecked,
    inputProvider: InputProvider = InputProvider
) = also { with(inputProvider) { checkAll(part1, part2) } }

fun Iterable<Puz<*, *>>.checkAll(
    part1: Any? = NotChecked,
    part2: Any? = NotChecked,
    input: String
) = checkAll(part1, part2) { _, _ -> PuzzleInput.of(input) }