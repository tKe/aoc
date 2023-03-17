package year2015

import aok.InputProvider
import aok.PuzDSL
import aoksp.AoKSolution


@AoKSolution
object Day11 : PuzDSL({

    fun String.nextPassword(): String {
        val chars = toCharArray()
        chars.indexOfFirst("ilo"::contains).takeIf { it >= 0 }?.also {
            for(i in (it+1) .. chars.lastIndex) chars[i] = 'z'
        }
        fun inc() {
            for(i in chars.indices.reversed()) {
                when(chars[i]) {
                    'z' -> chars[i] = 'a'
                    'h', 'k', 'n' -> chars[i] = chars[i] + 2
                    else -> chars[i]++
                }
                if(chars[i] != 'a') break
            }
        }
        fun containsStraight(): Boolean {
            var c = 0
            for(i in 0 until chars.lastIndex) {
                if(chars[i + 1] - chars[i] == 1) c++ else c = 0
                if(c >= 2) return true
            }
            return false
        }
        fun containsPairs(): Boolean {
            var c = 0
            var i = 0
            while(i < chars.lastIndex) {
                if(chars[i] == chars[i+1]) {
                    c++
                    if(c >= 2) return true
                    i++
                }
                i++
            }
            return false
        }
        fun valid() = containsPairs() && containsStraight()
        do { inc() } while(!valid())
        return String(chars)
    }


    part1 {
        input.nextPassword()
    }
    part2 {
        input.nextPassword().nextPassword()
    }
})

fun main() = solveDay(
    11,
)
