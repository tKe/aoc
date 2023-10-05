package year2018

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day03 : PuzDSL({

    data class Int2(val x: Int, val y: Int)
    data class Claim(val id: Int, val x: Int, val y: Int, val w: Int, val h: Int) : Iterable<Int2> {
        override fun iterator() = iterator  {
            repeat(w) { wx ->
                repeat(h) {hy ->
                    yield(Int2(x + wx, y + hy))
                }
            }
        }
    }

    val parseClaims = lineParser {
        val (id, x, y, w, h) = it.split("#"," @ ", ",", ": ", "x").mapNotNull(String::toIntOrNull)
        Claim(id, x, y, w, h)
    }

    fun List<Claim>.getDupes() = buildSet {
        val claimed = mutableSetOf<Int2>()
        this@getDupes.forEach { addAll(it.filterNot(claimed::add)) }
    }

    part1 {
        parseClaims().getDupes().size
    }

    part2 {
        val claims = parseClaims()
        val dupes = claims.getDupes()
        claims.single { it.none(dupes::contains) }.id
    }
})

fun main(): Unit = solveDay(3)
