package year2020

import aok.results
import aok.shouldGenerate
import io.kotest.core.spec.style.FreeSpec

class Day15Spec : FreeSpec({
    with(queryDay(15)) {
        "0,3,6" shouldGenerate results(part1 = 436, part2 = 175594)
    }
})
