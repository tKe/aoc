package aok

import kotlin.math.roundToInt
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
                        val result = it.value.toString().let {
                            if ('\n' in it) "\n" + it.lines().joinToString("\n") { line -> "\t\t$line" }
                            else it
                        }
                        println("\t $variant took ${it.duration} (${"%.2f".format(it.duration / fastest)}x): $result")
                    }
                }

                println("year $year day $day part 1")
                runPart { part1() }
                println("year $year day $day part 2")
                runPart { part2() }
            }
            println()
        }

// default inputs
fun Iterable<Puz<*, *>>.solveAll(runIterations: Int = 1) = with(InputProvider) { solveAll(runIterations) }
