package aok

import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe

private object Missing

context(DslDrivenSpec, List<Puz<*, *>>)
infix fun InputProvider.shouldGenerate(expected: ExpectedResults) =
    include("$this - ", shouldAll(this, expected.part1, expected.part2))

context(DslDrivenSpec, List<Puz<*, *>>)
infix fun String.shouldGenerate(expected: ExpectedResults) = InputProvider.raw(this) shouldGenerate expected

data class ExpectedResults(val part1: Any = Missing, val part2: Any = Missing)
fun results(part1: Any = Missing, part2: Any = Missing) = ExpectedResults(part1, part2)

fun List<Puz<*, *>>.shouldAll(
    inputProvider: InputProvider = InputProvider,
    part1: Any = Missing,
    part2: Any = Missing,
) = freeSpec {
    val year = map { it.year }.distinct().singleOrNull() ?: error("expect single year")
    val day = map { it.day }.distinct().singleOrNull() ?: error("expect single day")
    val input = inputProvider.forPuzzle(year, day)
    for (puz in this@shouldAll) {
        "$year-$day-${puz.variant}" - {
            if (part1 !== Missing) {
                "part1" {
                    with(input) {
                        runCatching { puz.part1() } shouldBeSuccess part1
                    }
                }
            }
            if (part2 !== Missing) {
                "part2" {
                    with(input) {
                        puz.part2() shouldBe part2
                    }
                }
            }
        }
    }
}