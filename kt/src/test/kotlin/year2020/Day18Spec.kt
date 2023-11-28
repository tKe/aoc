package year2020

import aok.results
import aok.shouldGenerate
import io.kotest.core.spec.style.FreeSpec

class Day18Spec : FreeSpec({
    with(queryDay(18)) {
        "1 + (2 * 3) + (4 * (5 + 6))" shouldGenerate results(part1 = 51L, part2 = 51L)
        "2 * 3 + (4 * 5)" shouldGenerate results(part1 = 26L, part2 = 46L)
        "5 + (8 * 3 + 9 + 3 * 4 * 3)" shouldGenerate results(part1 = 437L, part2 = 1445L)
        "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))" shouldGenerate results(part1 = 12240L, part2 = 669060L)
        "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2" shouldGenerate results(part1 = 13632L, part2 = 23340L)
    }
})
