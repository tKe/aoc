package aok

import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.result.shouldBeSuccess

private object Missing

fun List<Puz<*, *>>.shouldAll(
    part1: Any = Missing,
    part2: Any = Missing,
    inputProvider: InputProvider = InputProvider
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
                        runCatching { puz.part2() } shouldBeSuccess part2
                    }
                }
            }
        }
    }
}