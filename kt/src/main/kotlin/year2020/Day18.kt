package year2020

import aok.PuzDSL
import aoksp.AoKSolution
import year2020.Day18.eval

fun main() = solveDay(18)

@AoKSolution
object Day18 : PuzDSL({
    part1 { lines.sumOf { it.eval() } }
    part2 { lines.sumOf { it.eval(advanced = true) } }
}) {
    fun String.eval(advanced: Boolean = false): Long {
        val rpn = buildList {
            val ops = ArrayDeque<Char>()
            for (c in this@eval) when (c) {
                in '0'..'9' -> add(c)
                '(' -> ops.addFirst(c)
                ')' -> while (ops.isNotEmpty()) when (val op = ops.removeFirst()) {
                    '(' -> break
                    else -> add(op)
                }

                '+', '*' -> {
                    if (!advanced || c == '*')
                        while (ops.isNotEmpty() && ops.first() != '(')
                            add(ops.removeFirst())
                    ops.addFirst(c)
                }
            }
            while (ops.isNotEmpty()) add(ops.removeFirst())
        }

        val stack = ArrayDeque<Long>()
        for (c in rpn) when (c) {
            in '0'..'9' -> stack.addFirst(c.digitToInt().toLong())
            '+' -> stack.addFirst(stack.removeFirst() + stack.removeFirst())
            '*' -> stack.addFirst(stack.removeFirst() * stack.removeFirst())
        }

        return stack.single()
    }
}