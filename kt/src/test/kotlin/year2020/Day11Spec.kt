package year2020

import aok.InputProvider
import aok.results
import aok.shouldGenerate
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class Day11Spec : FreeSpec({

    "occupiedVisible" - {
        "second example" {
            Day11.Seating(
                """
                    .............
                    .L.L.#.#.#.#.
                    .............
                """.trimIndent()
            ).run {
                occupiedVisible(1, 1) shouldBe 0
                occupiedVisible(3, 1) shouldBe 1
                occupiedVisible(5, 1) shouldBe 1
                occupiedVisible(7, 1) shouldBe 2
            }
        }
    }

    queryDay(11).apply {
        InputProvider.Example shouldGenerate results(part1 = 37, part2 = 26)
    }
})