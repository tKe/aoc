package aok

import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

private object Missing

context(DslDrivenSpec, List<Puz<*, *>>)
infix fun InputProvider.shouldGenerate(expected: ExpectedResults) =
    include("$this - ", testAllSolutions(this, expected.part1, expected.part2))

context(DslDrivenSpec, List<Puz<*, *>>)
infix fun String.shouldGenerate(expected: ExpectedResults) = InputProvider.raw(this) shouldGenerate expected

data class ExpectedResults(val part1: Any = Missing, val part2: Any = Missing)

fun results(part1: Any = Missing, part2: Any = Missing) = ExpectedResults(part1, part2)

fun List<Puz<*, *>>.testAllSolutions(
    inputProvider: InputProvider = InputProvider,
    part1: Any = Missing,
    part2: Any = Missing,
) = freeSpec {
    val year = map { it.year }.distinct().singleOrNull() ?: error("expect single year")
    val day = map { it.day }.distinct().singleOrNull() ?: error("expect single day")
    val input = inputProvider.forPuzzle(year, day)
    for (puz in this@testAllSolutions) {
        "$year-$day-${puz.variant} [$inputProvider]" - {
            with(input) {
                "part1".config(enabled = part1 !== Missing) {
                    puz.part1().toString() shouldBe part1.toString()
                }
                "part2".config(enabled = part2 !== Missing) {
                    puz.part2().toString() shouldBe part2.toString()
                }
            }
        }
    }
}
