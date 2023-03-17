package year2016

import aok.PuzDSL
import aok.Warmup
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day18 : PuzDSL({
    fun String.nextRow() = ".$this.".windowedSequence(3)
        .joinToString("") { if (it[0] != it[2]) "^" else "." }

    fun String.generateGrid(rows: Int) = generateSequence(this, String::nextRow).take(rows)

    part1 {
        input.trim().generateGrid(40).sumOf { it.count('.'::equals) }
    }
    part2 {
        input.trim().generateGrid(400000).sumOf { it.count('.'::equals) }
    }
})

@AoKSolution
object Day18CharArray : PuzDSL({
    fun String.nextRow() = String(CharArray(length) {
        when(it) {
            0 -> this[it + 1]
            lastIndex -> this[it - 1]
            else -> if(this[it - 1] != this[it + 1]) '^' else '.'
        }
    })

    fun String.generateGrid(rows: Int) = generateSequence(this, String::nextRow).take(rows)

    part1 {
        input.trim().generateGrid(40).sumOf { it.count('.'::equals) }
    }
    part2 {
        input.trim().generateGrid(400000).sumOf { it.count('.'::equals) }
    }
})

@AoKSolution
object Day18BooleanArrays : PuzDSL({
    fun BooleanArray.updateRow() {
        var prev = false
        for (idx in indices) {
            val old = this[idx]
            when (idx) {
                lastIndex -> this[idx] = prev
                else -> this[idx] = this[idx + 1] != prev
            }
            prev = old
        }
    }

    fun String.countSafe(rows: Int): Int {
        val row = BooleanArray(length) { get(it) == '^' }
        var count = row.count { !it }
        repeat(rows - 1) {
            row.updateRow()
            count += row.count { !it }
        }
        return count
    }

    part1 {
        input.trim().countSafe(40)
    }
    part2 {
        input.trim().countSafe(400000)
    }
})
@AoKSolution
object Day18BooleanArraysSeq : PuzDSL({
    fun BooleanArray.updateRow() {
        foldIndexed(false) { idx, prev, next ->
            when (idx) {
                lastIndex -> this[idx] = prev
                else -> this[idx] = this[idx + 1] != prev
            }
            next
        }
    }

    fun String.countSafe(rows: Int) =
        generateSequence(BooleanArray(length) { get(it) == '^' }) { it.apply { updateRow() } }
            .take(rows)
            .sumOf { it.count { !it } }

    part1 {
        lines.first().countSafe(40)
    }
    part2 {
        lines.first().countSafe(400000)
    }
})

fun main() = solveDay(
    18,
    warmup = Warmup.eachFor(5.seconds), runs = 5
//    input = InputProvider.raw("10000")
)
