package year2020

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(
    8,
)

@AoKSolution
object Day08 : PuzDSL({
    part1 {
        var acc = 0
        var instr = 0
        val executed = mutableSetOf<Int>()
        while (executed.add(instr)) {
            val (op, arg) = lines[instr].split(" ")
            when (op) {
                "nop" -> instr++
                "jmp" -> instr += arg.toInt()
                "acc" -> {
                    acc += arg.toInt();
                    instr++
                }
            }
        }
        acc
    }

    part2 {
        fun String.flip() = when(this) {
            "nop" -> "jmp"
            "jmp" -> "nop"
            else -> this
        }
        fun execFlipped(flip: Int): Int? {
            var acc = 0
            var instr = 0
            val executed = mutableSetOf<Int>()
            while (instr in lines.indices) {
                if(!executed.add(instr)) return null
                val (op, arg) = lines[instr].split(" ")
                when (if(instr == flip) op.flip() else op) {
                    "nop" -> instr++
                    "jmp" -> instr += arg.toInt()
                    "acc" -> {
                        acc += arg.toInt();
                        instr++
                    }
                }
            }
            return acc
        }

        lines.indices.firstNotNullOf { execFlipped(it) }
    }
})

