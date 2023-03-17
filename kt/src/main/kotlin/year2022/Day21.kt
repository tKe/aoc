package year2022

import aok.PuzzleInput
import aoksp.AoKSolution
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

fun main(): Unit = solveDay(21)

@AoKSolution
object Day21 {

    context(PuzzleInput)
    private fun evaluate(includeHuman: Boolean = true): Expr {
        val tasks = lines.associate {
            val (monkey, task) = it.split(": ")
            monkey to task
        }

        fun eval(monkey: String): Expr =
            if (!includeHuman && monkey == "humn") Expr.Unknown
            else tasks[monkey]!!.let { task ->
                if (task[0].isLowerCase()) {
                    val (left, op, right) = task.split(' ')
                    when (op) {
                        "+" -> eval(left) + eval(right)
                        "-" -> eval(left) - eval(right)
                        "*" -> eval(left) * eval(right)
                        "/" -> eval(left) / eval(right)
                        else -> error("unhandled operation '$op'")
                    }
                } else Expr.Const(task.toLong())
            }
        return eval("root")
    }

    context(PuzzleInput)
    fun part1() = (evaluate() as Expr.Const).value

    context(PuzzleInput)
    fun part2() = with(evaluate(includeHuman = false)) {
        check(this is Expr.Operation)
        (left - right).solve(0)
    }

    private tailrec fun Expr.solve(expect: Long): Long = when {
        this is Expr.Unknown -> expect
        this !is Expr.Operation -> error("unable to solve $this")
        left is Expr.Const -> right.solve(op.right(expect, left.value))
        right is Expr.Const -> left.solve(op.left(expect, right.value))
        else -> error("can't eval one side of the equation")
    }

    private sealed class Operator(
        val result: (left: Long, right: Long) -> Long,
        val left: (result: Long, right: Long) -> Long,
        val right: (result: Long, left: Long) -> Long = left,
    ) {
        object Add : Operator(Long::plus, Long::minus)
        object Subtract : Operator(Long::minus, Long::plus, { result, left -> left - result })
        object Multiply : Operator(Long::times, Long::div)
        object Divide : Operator(Long::div, Long::times, { result, left -> left / result })
    }

    private sealed interface Expr {
        object Unknown : Expr
        class Operation(val left: Expr, val op: Operator, val right: Expr) : Expr
        class Const(val value: Long) : Expr
    }

    private operator fun Expr.plus(right: Expr) = Operator.Add(this, right)
    private operator fun Expr.minus(right: Expr) = Operator.Subtract(this, right)
    private operator fun Expr.times(right: Expr) = Operator.Multiply(this, right)
    private operator fun Expr.div(right: Expr) = Operator.Divide(this, right)
    private operator fun Operator.invoke(left: Expr, right: Expr) = when {
        left !is Expr.Const || right !is Expr.Const -> Expr.Operation(left, this, right)
        else -> Expr.Const(this.result.invoke(left.value, right.value))
    }
}

@AoKSolution
object Day21Shouting {
    context (PuzzleInput)
    suspend fun part1(): Long = coroutineScope {
        val room = MutableSharedFlow<Pair<String, Long>>(replay = lines.size)

        suspend infix fun String.shouts(value: Long) = room.emit(this to value)
        suspend fun listenFor(vararg monkeys: String): List<Long> {
            val heard = mutableMapOf<String, Long>()
            room.takeWhile {
                if (it.first in monkeys) heard[it.first] = it.second
                heard.size < monkeys.size
            }.collect()
            return monkeys.map(heard::getValue)
        }

        lines.forEach {
            val (monkey, job) = it.split(": ")
            if (!job[0].isLowerCase()) monkey shouts job.toLong()
            else {
                val (leftMonkey, op, rightMonkey) = job.split(" ")
                launch {
                    val (l, r) = listenFor(leftMonkey, rightMonkey)
                    val result = when (op) {
                        "+" -> l + r
                        "-" -> l - r
                        "/" -> l / r
                        "*" -> l * r
                        else -> error("unsupported operation '$op'")
                    }
                    monkey shouts result
                }
            }
        }

        listenFor("root").first()
    }
}
