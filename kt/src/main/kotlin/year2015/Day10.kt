package year2015

import aok.PuzDSL
import aoksp.AoKSolution


@AoKSolution
object Day10 : PuzDSL({
    data class CharCount(val char: Char, val count: Int = 1)

    operator fun CharCount.inc() = copy(count = count + 1)

    fun MutableList<CharCount>.add(char: Char) {
        if (isEmpty() || last().char != char) add(CharCount(char))
        else this[lastIndex] = last().inc()
    }

    fun String.lookAndSay() = buildList<CharCount> {
        for (next in this@lookAndSay) add(next)
    }

    fun Iterable<CharCount>.lookAndSay() = buildList<CharCount> {
        for (cc in this@lookAndSay) {
            add(cc.count.digitToChar())
            add(cc.char)
        }
    }

    part1 {
        generateSequence(input.lookAndSay()) { it.lookAndSay() }
            .elementAt(40)
            .sumOf { it.count }
    }

    part2 {
        generateSequence(input.lookAndSay(), List<CharCount>::lookAndSay)
            .elementAt(50)
            .sumOf { it.count }
    }
})

fun main() = solveDay(
    10,
//    input = InputProvider.raw("1")
)
