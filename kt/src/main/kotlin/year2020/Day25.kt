package year2020

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(25)

@AoKSolution
object Day25 : PuzDSL({
    part1(lineParser(String::toLong)) { (cardPub, doorPub) ->
        fun guessLoop(pubKey: Long): Int {
            var looped = 0
            var encrypted = 1L
            while(encrypted != pubKey) {
                encrypted *= 7
                encrypted %= 20201227
                looped++
            }
            return looped
        }

        val cardLoop = guessLoop(cardPub)

        fun encrypt(subject: Long, loopSize: Int): Long {
            var encrypted = 1L
            repeat(loopSize){
                encrypted *= subject
                encrypted %= 20201227
            }
            return encrypted
        }

        encrypt(doorPub, cardLoop)
    }
})