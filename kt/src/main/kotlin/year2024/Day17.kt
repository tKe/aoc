package year2024

import aok.PuzzleInput
import aok.checkAll
import aok.lines
import aok.solveAll
import aok.warmup
import aoksp.AoKSolution
import utils.splitInts
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day17 {
    data class Input(val a: Long, val b: Long = 0, val c: Long = 0)
    private infix fun Input.eval(program: List<Int>) = iterator {
        val r = longArrayOf(0, 1, 2, 3, a, b, c)
        var i = 0
        while (i in program.indices) {
            val opCode = program[i++]
            val operand = program[i++]
            when (opCode) {
                0 -> r[4] = r[4] shr r[operand].toInt()
                1 -> r[5] = r[5] xor operand.toLong()
                2 -> r[5] = r[operand] % 8
                3 -> if (r[4] != 0L) i = operand
                4 -> r[5] = r[5] xor r[6]
                5 -> yield((r[operand] % 8).toInt())
                6 -> r[5] = r[4] shr r[operand].toInt()
                7 -> r[6] = r[4] shr r[operand].toInt()
            }
        }
    }

    context(_: PuzzleInput) fun part1() = solve { state, program ->
        Iterable { state eval program }.joinToString(",")
    }

    context(_: PuzzleInput) fun part2() = solve { _, program ->
        infix fun <T> Iterator<T>.notMatch(list: ListIterator<T>): Boolean {
            while (hasNext()) if (!list.hasNext() || next() != list.next()) return true
            return list.hasNext()
        }

        var a = 0L
        for (i in program.indices.reversed()) {
            a = a shl 3
            while (Input(a) eval program notMatch program.listIterator(i)) a++
        }
        a
    }

    context(_: PuzzleInput)
    inline fun <R> solve(eval: (Input, program: List<Int>) -> R): R {
        val registers = lines.take(3).map { it.substringAfter(": ").toLong() }.let { (a, b, c) -> Input(a, b, c) }
        val program = lines.last().substringAfter(": ").splitInts(',')
        return eval(registers, program)
    }
}

fun main() {
    queryDay(17)
        .checkAll(
            part1 = "4,6,3,5,6,3,5,2,1,0",
            input = {
                """
                Register A: 729
                Register B: 0
                Register C: 0

                Program: 0,1,5,4,3,0
            """.trimIndent()
            })
        .checkAll(
            part2 = 117440L,
            input = {
                """
            Register A: 2024
            Register B: 0
            Register C: 0

            Program: 0,3,5,4,3,0
        """.trimIndent()
            })
        .warmup(5.seconds)
        .checkAll(part1 = "6,5,7,4,5,7,3,1,0", part2 = 105875099912602L)
        .solveAll()
}
