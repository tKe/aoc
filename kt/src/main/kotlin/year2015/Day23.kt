package year2015

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

@AoKSolution
object Day23 : PuzDSL({
    data class Mem(val i: UInt = 0u, val a: UInt = 0u, val b: UInt = 0u)


    fun mutate(reg: String, op: (UInt) -> UInt): Mem.() -> Mem = when(reg) {
        "a" -> ({ copy(i = i + 1u, a = op(a)) })
        "b" -> ({ copy(i = i + 1u, b = op(b)) })
        else -> error("unknown register")
    }
    fun jump(op: Mem.() -> Int): Mem.() -> Mem =
        { copy(i = (i.toInt() + op()).toUInt()) }

    fun PuzzleInput.parseInstructions() = lines.map { cmd ->
        val (instr, arg) = cmd.split(" ", limit = 2)
        when (instr) {
            "hlf" -> mutate(arg) { it / 2u }
            "tpl" -> mutate(arg) { it * 3u }
            "inc" -> mutate(arg) { it + 1u }
            "jmp" -> arg.toInt().let { jump { it } }
            else -> {
                val args = arg.split(", ")
                val ofs = args[1].toInt()
                val reg: Mem.() -> UInt = if(args[0] == "a") Mem::a else Mem::b

                when (instr) {
                    "jio" -> jump { if(reg() == 1u) ofs else 1 }
                    "jie" -> jump { if(reg() % 2u == 0u) ofs else 1 }
                    else -> error("Unknown instruction '$cmd'")
                }
            }
        }
    }

    fun List<Mem.() -> Mem>.execute(mem: Mem = Mem()) =
        generateSequence(mem) { m ->
            getOrNull(m.i.toInt())?.invoke(m)
        }.last()

    part1 { parseInstructions().execute().b }
    part2 { parseInstructions().execute(Mem(a = 1u)).b }
})

fun main() = solveDay(
    23,
)
