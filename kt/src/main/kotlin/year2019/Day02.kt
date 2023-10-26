package year2019

import aok.InputProvider
import aok.PuzDSL
import aoksp.AoKSolution
import utils.splitIntsNotNull

fun main(): Unit = solveDay(
        2,
//        input = InputProvider.raw("1,9,10,3,2,3,11,0,99,30,40,50"),
)

@AoKSolution
object Day02 : PuzDSL({
    fun IntArray.process(noun: Int = 12, verb: Int = 2) = copyOf().also {
        it[1] = noun
        it[2] = verb
        var i = 0
        while (true) {
            when (val opcode = it[i++]) {
                1 -> {
                    val a = it[i++]
                    val b = it[i++]
                    val c = it[i++]
                    it[c] = it[a] + it[b]
                }

                2 -> {
                    val a = it[i++]
                    val b = it[i++]
                    val c = it[i++]
                    it[c] = it[a] * it[b]
                }

                99 -> break
                else -> error("unhandled opcode '$opcode'")
            }
        }
    }[0]

    val parser = parser { input.splitIntsNotNull(",").toIntArray() }

    part1(parser) { prog ->
        prog.process()
    }

    part2(parser) { prog ->
        for (noun in 0..99)
            for (verb in 0..99)
                if (prog.process(noun, verb) == 19690720)
                    return@part2 100 * noun + verb
        error("no solution found")
    }
})
