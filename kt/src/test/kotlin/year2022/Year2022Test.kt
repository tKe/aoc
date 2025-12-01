package year2022

import aok.InputProvider
import aok.Puz
import aok.PuzzleInput
import aok.withInput
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.scopes.FreeSpecContainerScope
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe

class Year2022Test : FreeSpec({
    "examples" - {
        with(InputProvider.Example) {
            queryDay(1).shouldAll(24000, 45000)
            queryDay(2).shouldAll(15, 12)
            queryDay(3).shouldAll(157, 70)
            queryDay(4).shouldAll(2, 4)
            queryDay(5).shouldAll("CMZ", "MCD")
            queryDay(6).shouldAll(7, 1)
        }
//
        queryDay(6).forEach {
            withInput("mjqjpqmgbljsphdztnvjfqwrcgsmlb") { it.part2() shouldBe 19 }
            withInput("bvwbjplbgvbhsrlpgdmjqwftvncz") { it.part2() shouldBe 23 }
            withInput("nppdvjthqldpwncqszvftbrmjlhg") { it.part2() shouldBe 23 }
            withInput("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") { it.part2() shouldBe 29 }
            withInput("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") { it.part2() shouldBe 26 }
        }
    }

    "inputs" - {
        with(InputProvider) {
            queryDay(1).shouldAll(69795, 208437)
            queryDay(2).shouldAll(9651, 10560)
            queryDay(3).shouldAll(7980, 2881)
            queryDay(4).shouldAll(657, 938)
            queryDay(5).shouldAll("VWLCWGSDQ", "TCGLQSLPW")
        }
    }
})

context(input: InputProvider, spec: FreeSpecContainerScope)
suspend inline fun Iterable<Puz<*, *>>.shouldAll(expected1: Any, expected2: Any) =
    shouldAll { input, puzzle ->
        with(input) {
            "part1" {
                runCatching { puzzle.part1() } shouldBeSuccess expected1
            }
            "part2" {
                runCatching { puzzle.part2() } shouldBeSuccess expected2
            }
        }
    }

context(spec: FreeSpecContainerScope, input: InputProvider)
suspend fun <A, B> Iterable<Puz<A, B>>.shouldAll(test: suspend FreeSpecContainerScope.(PuzzleInput, Puz<A, B>) -> Unit) =
    spec.run {
        groupBy { it.year to it.day }
            .forEach { (d, puzzles) ->
                val (year, day) = d
                "Year $year Day $day" - {
                    val input = input.forPuzzle(year, day)
                    puzzles.forEach { puzzle ->
                        puzzle.variant - {
                            test(input, puzzle)
                        }
                    }
                }
            }
    }
