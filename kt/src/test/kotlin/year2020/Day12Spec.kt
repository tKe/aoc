package year2020

import aok.InputProvider
import aok.results
import aok.shouldGenerate
import io.kotest.core.spec.style.FreeSpec

class Day12Spec : FreeSpec({
    queryDay(12).apply {
        InputProvider.Example shouldGenerate results(part1 = 25, part2 = 286)
    }
})