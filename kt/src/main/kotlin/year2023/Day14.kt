package year2023

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(14)

@AoKSolution
object Day14 : PuzDSL({
    fun CharArray.rollRight() {
        var start = 0
        for (idx in indices) if (this[idx] == '#') {
            sort(start, idx)
            start = idx + 1
        }
        sort(start, size)
    }

    fun List<CharArray>.rotateAndRoll() = first().indices.map { x ->
        CharArray(size) { this@rotateAndRoll[lastIndex - it][x] }
            .apply(CharArray::rollRight)
    }

    fun List<CharArray>.rightLoad() = sumOf { it.indices.sumOf { x -> if (it[x] == 'O') x + 1 else 0 } }
    fun List<CharArray>.topLoad() = indices.sumOf { y -> (lastIndex - y + 1) * get(y).count('O'::equals) }

    val parse = lineParser { it.toCharArray() }
    part1(parse) { it.rotateAndRoll().rightLoad() }
    part2(parse) { initial ->
        val history = ArrayList<Pair<Int, Int>>()
        val loopStart = generateSequence(initial) {
            it.rotateAndRoll().rotateAndRoll().rotateAndRoll().rotateAndRoll()
        }.firstNotNullOf { grid ->
            val hash = grid.fold(0) { acc, chars -> acc * 31 + chars.contentHashCode() }
            val key = hash to grid.topLoad()
            history.lastIndexOf(key).takeIf { it >= 0 }
                ?: history.add(key).let { null }
        }
        val remainingSpins = 1_000_000_000 - history.size
        val loopLength = history.size - loopStart
        history[loopStart + remainingSpins % loopLength].second
    }
})
