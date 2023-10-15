package year2018

import aok.PuzDSL
import aoksp.AoKSolution
import year2018.Day16.Op
import year2018.Day16.eqrr
import year2018.Day16.reg
import year2018.Day19.Instr

fun main(): Unit = solveDay(
    21,
//    warmup = aok.Warmup.iterations(30), runs = 50,
//    input = aok.InputProvider.raw(
//        """
//            #ip 0
//            seti 5 0 1
//            seti 6 0 2
//            addi 0 1 0
//            addr 1 2 3
//            setr 1 0 0
//            seti 8 0 4
//            seti 9 0 5
//        """.trimIndent()
//    )
)

@AoKSolution
object Day21 : PuzDSL({

    @Suppress("DuplicatedCode")
    val parse = parser {
        val opsByName = Op.all.associateBy { "$it" }
        val instructionBinding = lines.first()
        val instructions = lines.drop(1).map { line ->
            val (op, a, b, c) = line.split(" ")
            Instr(opsByName[op] ?: error("missing op '$op'"), a.toInt(), b.toInt(), c.toInt())
        }
        val instrReg = instructionBinding.split(' ').last().toInt()
        instrReg to instructions
    }

    fun checks(ip: Int, instrs: List<Instr>): Sequence<Int> {
        // find an instruction that checks 0.reg against another reg
        val chkInstr = instrs.indexOfFirst { (op, a, b, c) ->
            op == eqrr && (a == 0 || b == 0)
        }.also { require(it >= 0) { "check instr not found" } }
        // find the other reg on which 0.reg is checked
        val checkReg = instrs[chkInstr].let { (_, a, b) -> if(a == 0) b else a }
        // stream out values of the other reg
        return sequence {
            with(IntArray(6)) {
                while (ip.reg in instrs.indices) {
                    if (ip.reg == chkInstr) yield(checkReg.reg)
                    instrs[ip.reg].execute()
                    ip.reg++
                }
            }
        }
    }

    part1(parse) { (ip, instrs) ->
        checks(ip, instrs).first()
    }
    part2(parse) { (ip, instrs) ->
        // could really do with an optimized instruction
        // solves without optimization in ~12s
        // TODO: do it.
        checks(ip, instrs).takeWhile(mutableSetOf<Int>()::add).last()
    }
})