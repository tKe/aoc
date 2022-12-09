package year2022

import InputScope
import InputScopeProvider
import Puz
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.scopes.FreeSpecContainerScope
import io.kotest.matchers.result.shouldBeSuccess

class Year2022Test : FreeSpec({
    "examples" - {
        with(InputScopeProvider.mapping("input.txt" to "example.txt")) {
            checkThat<Day01Puz, _, _>(24000, 45000)
            checkThat<Day02Puz, _, _>(15, 12)
            checkThat<Day03DSL, _, _>(157, 70)
            checkThat<Day04DSL, _, _>(2, 4)
            checkThat<Day05DSL, _, _>("CMZ", "MCD")
            checkThat<Day06DSL, _, _>(7, 1)
        }
//
//        Puz.getAll<Day06DSL, _, _>().forEach {
//            withInput("input.txt" to "mjqjpqmgbljsphdztnvjfqwrcgsmlb") { it.part2() shouldBe 19 }
//            withInput("input.txt" to "bvwbjplbgvbhsrlpgdmjqwftvncz") { it.part2() shouldBe 23 }
//            withInput("input.txt" to "nppdvjthqldpwncqszvftbrmjlhg") { it.part2() shouldBe 23 }
//            withInput("input.txt" to "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") { it.part2() shouldBe 29 }
//            withInput("input.txt" to "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") { it.part2() shouldBe 26 }
//        }
    }

    "inputs" - {
        with(InputScopeProvider) {
            checkThat<Day01Puz, _, _>(69795, 208437)
            checkThat<Day02Puz, _, _>(9651, 10560)
            checkThat<Day03DSL, _, _>(7980, 2881)
            checkThat<Day04DSL, _, _>(657, 938)
            checkThat<Day05DSL, _, _>("VWLCWGSDQ", "TCGLQSLPW")
        }
    }
})

context(InputScopeProvider)
suspend inline fun <reified T : Puz<A, B>, A, B> FreeSpecContainerScope.checkThat(part1: A, part2: B) =
    Puz.getAll<T, A, B>().shouldAll { input, puzzle ->
        with(input) {
            "part1" {
                runCatching { puzzle.part1() } shouldBeSuccess part1
            }
            "part2" {
                runCatching { puzzle.part2() } shouldBeSuccess part2
            }
        }
    }

context(FreeSpecContainerScope, InputScopeProvider)
suspend fun <A, B> Iterable<Puz<A, B>>.shouldAll(test: suspend FreeSpecContainerScope.(InputScope, Puz<A, B>) -> Unit) =
    groupBy { it.year to it.day }
        .forEach { (d, puzzles) ->
            val (year, day) = d
            "Year $year Day $day" - {
                val input = forPuzzle(year, day)
                puzzles.forEach { puzzle ->
                    puzzle.variant - {
                        test(input, puzzle)
                    }
                }
            }
        }
