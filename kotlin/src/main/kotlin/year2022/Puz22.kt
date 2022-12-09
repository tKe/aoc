package year2022

import Puz
import PuzYear
import PuzYearDSL
import PuzzleDefinition
import solveAll

sealed interface Puz22<A, B> : Puz<A, B>

sealed class Puz22Base<P1, P2>(day: Int, variant: String?) : Puz22<P1, P2>,
    PuzYear<P1, P2>(2022, day, variant)

sealed class Puz22DSL<A, B>(day: Int, variant: String? = null, def: PuzzleDefinition<A, B>) : Puz22<A, B>,
    PuzYearDSL<A, B>(2022, day, variant, def)

fun main() = solveAll<Puz22<Any, Any>>()
