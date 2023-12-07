package year2023

import aok.InputProvider.Companion.Example
import aok.testAllSolutions
import io.kotest.core.spec.style.FreeSpec

class Day07Spec : FreeSpec({
    include(queryDay(7).testAllSolutions(inputProvider = Example, part1 = 6440, part2 = 5905))
    include(queryDay(7).testAllSolutions(part1 = 248422077, part2 = 249817836))
})
