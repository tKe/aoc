package year2018

import aok.PuzDSL
import aoksp.AoKSolution
import year2018.Day16.Op
import year2018.Day16.reg
import year2018.Day19.optimizeWith

fun main(): Unit = solveDay(
    19,
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
object Day19 : PuzDSL({

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

    fun List<Instr>.execute(ip: Int, vararg initialValues: Int) = IntArray(6) {
        initialValues.getOrNull(it) ?: 0
    }.apply {
        while (ip.reg in this@execute.indices) {
            this@execute[ip.reg].execute()
            ip.reg++
        }
    }

    part1(parse) { (ip, instrs) ->
        instrs.execute(ip)[0]
    }

    part2(parse) { (instrReg, instrs) ->
        // add divisors custom-op
        // increases register C by register A if register A is an exact divisor of register B
        val optimized = instrs.optimizeWith(
            Op.custom("addd", fun IntArray.(a: Int, b: Int, c: Int) {
                if (b.reg % a.reg == 0) c.reg += a.reg
            }),
            instrReg,
            "seti 1 * x", // seti 1 4 3
            "mulr a x y", // mulr 1 3 5
            "eqrr y b y", // eqrr 5 4 5
            "addr z i i", // addr 5 2 2
            "addi i 1 i", // addi 2 1 2
            "addr a c c", // addr 1 0 0
            "addi x 1 x", // addi 3 1 3
            "gtrr x b y", // gtrr 3 4 5
            "addr i y i", // addr 2 5 2
            "seti 2 * i", // seti 2 4 2
        )

        // instrs.zip(optimized) { old, new ->
        //     if (old != new) println("$old -> $new")
        //     else println("$old")
        // }

        optimized.execute(instrReg, 1)[0]
    }
}) {
    data class Instr(val op: Op, val a: Int, val b: Int, val c: Int) {
        override fun toString(): String {
            return "[$op $a $b $c]"
        }

        context(IntArray)
        fun execute() = op(a, b, c)
    }

    /**
     * replace a matched set of instructions with an alternative instruction
     * matchers are of the form "<op> <pattern> <pattern> <pattern>"
     * where <pattern> is one of:
     *  - a numeric constant -> must match exactly
     *  - *  -> always matches
     *  - i -> must match the instruction register (ip)
     *  - or a placeholder string
     *      - all occurrences of a placeholder must match across all matchers
     *      - if a, b or c -> interpreted as the values for the replacement instruction
     */
    fun List<Instr>.optimizeWith(
        op: Op,
        ip: Int,
        vararg matchers: String
    ): List<Instr> {
        val vars = mutableMapOf<String, Int>()
        val start = windowed(matchers.size, step = 1, partialWindows = false).indexOfFirst {
            vars.clear()
            vars["i"] = ip

            fun checkVar(expect: String, actual: Int) = when (expect) {
                "*", "$actual" -> true
                else -> actual == vars.getOrPut(expect) { actual }
            }

            infix fun Instr.matches(matcher: String) = matcher.split(" ").let { (op, a, b, c) ->
                op == this.op.toString() && checkVar(a, this.a) && checkVar(b, this.b) && checkVar(c, this.c)
            }

            it.zip(matchers.asIterable())
                .all { (instr, matcher) -> instr matches matcher }
        }.also { require(it >= 0) { "operation not found to optimize" } }

        return subList(0, start) + buildList {
            add(Instr(op, vars["a"] ?: 0, vars["b"] ?: 0, vars["c"] ?: 0))
            add(Instr(Day16.seti, start + matchers.lastIndex, 0, ip))
            repeat(matchers.size - 2) { add(Instr(Day16.noop, 0, 0, 0)) }
        } + subList(start + matchers.size, size)
    }
}