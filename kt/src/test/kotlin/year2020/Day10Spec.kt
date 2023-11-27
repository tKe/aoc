package year2020

import aok.InputProvider.Companion.Default
import aok.InputProvider.Companion.Example
import aok.InputProvider.Companion.forFile
import aok.results
import aok.shouldGenerate
import io.kotest.core.spec.style.FreeSpec

class Day10Spec : FreeSpec({
    queryDay(10).apply {
        Default shouldGenerate results(part1 = 1656, part2 = 56693912375296)
        Example shouldGenerate results(part1 = 35, part2 = 8L)
        forFile("example-long.txt") shouldGenerate results(part1 = 220, part2 = 19208L)
    }
})

