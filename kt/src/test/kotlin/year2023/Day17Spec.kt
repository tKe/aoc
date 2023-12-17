package year2023

import aok.testAllSolutions
import io.kotest.core.spec.style.FreeSpec

class Day17Spec : FreeSpec({
    include(queryDay(17).testAllSolutions(part1 = 1238, part2 = 1362))
})
