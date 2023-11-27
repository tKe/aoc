package year2020

import aok.results
import aok.shouldGenerate
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import year2020.Day13.LinearCongruence
import year2020.Day13.solve

class Day13Spec : FreeSpec({
    "chinese remainder" - {
        "example" {
            listOf(
                LinearCongruence(2, 3),
                LinearCongruence(3, 5),
                LinearCongruence(2, 7),
            ).solve() shouldBe 23L
        }
    }

    queryDay(13).apply {
        "17,x,13,19" shouldGenerate results(part2 = 3417L)
    }
})
