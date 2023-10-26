package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import arrow.core.andThen

fun main(): Unit = solveDay(
    6,
//    input = aok.InputProvider.raw(
//        """
//            COM)B
//            B)C
//            C)D
//            D)E
//            E)F
//            B)G
//            G)H
//            D)I
//            E)J
//            J)K
//            K)L
//            K)YOU
//            I)SAN
//        """.trimIndent()
//    )
)

@AoKSolution
object Day06 : PuzDSL({
    val parseOrbits = lineParser {
        val (a, b) = it.split(")")
        b to a
    }.andThen { it.toMap() }

    part1(parseOrbits) { orbits ->
        orbits.keys.sumOf { generateSequence(it, orbits::get).drop(1).count() }
    }

    part2(parseOrbits) { orbits ->
        val myOrbits = generateSequence("YOU", orbits::get).drop(1)
        val sanOrbits = generateSequence("SAN", orbits::get).drop(1)
        val commonOrbits = myOrbits.toList().asReversed().zip(sanOrbits.toList().asReversed()).takeWhile { (a, b) -> a == b }.count()
        myOrbits.count() + sanOrbits.count() - 2*commonOrbits
    }
})

