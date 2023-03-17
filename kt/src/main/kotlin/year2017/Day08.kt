package year2017

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day08 : PuzDSL({
    fun Map<String, Int>.check(test: String): Boolean {
        val (reg, operator, operand) = test.split(" ")
        val actual = (this[reg] ?: 0)
        val expected = operand.toInt()
        return when (operator) {
            "<" -> actual < expected
            ">" -> actual > expected
            "<=" -> actual <= expected
            ">=" -> actual >= expected
            "==" -> actual == expected
            "!=" -> actual != expected
            else -> error("unsupported operator '$operator'")
        }
    }
    fun Map<String, Int>.apply(mutation: String) = this + mutation.split(" ").let { (reg, operation, delta) ->
        reg to (this[reg] ?: 0) + when(operation) {
            "inc" -> delta.toInt()
            "dec" -> -delta.toInt()
            else -> error("unsupported operation '$operation'")
        }
    }

    part1 {
        lines.fold(emptyMap<String, Int>()) { regs, it ->
            val (mutation, test) = it.split(" if ")
            if(regs.check(test)) regs.apply(mutation) else regs
        }.values.max()
    }

    part2 {
        lines.runningFold(emptyMap<String, Int>()) { regs, it ->
            val (mutation, test) = it.split(" if ")
            if(regs.check(test)) regs.apply(mutation) else regs
        }.maxOf { it.values.maxOrNull() ?: 0 }
    }
})

fun main(): Unit = solveDay(8)
