package year2020

import aok.results
import aok.shouldGenerate
import io.kotest.core.spec.style.FreeSpec

class Day17Spec : FreeSpec({

    with(queryDay(17)) {
        """
        .#.
        ..#
        ###
    """.trimIndent() shouldGenerate results(part1 = 112, part2 = 848)
    }
})
