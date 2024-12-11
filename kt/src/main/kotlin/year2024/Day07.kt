package year2024

import aok.PuzDSL
import aok.PuzzleInput
import aok.checkAll
import aok.solveAll
import aok.warmup
import aok.warmupEach
import aoksp.AoKSolution
import utils.splitIntsNotNull
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day07 : PuzDSL({
    fun PuzzleInput.calc(vararg ops: (Long, Int) -> Long) = lines.map {
        val (result, operands) = it.split(": ", limit = 2)
        result.toLong() to operands.splitIntsNotNull()
    }.filter { (result, operands) ->
        fun check(rem: List<Int>, acc: Long = 0L): Boolean {
            val head = rem.first()
            val tail = rem.drop(1)
            return ops.any {
                when {
                    tail.isEmpty() -> it(acc, head) == result
                    else -> check(tail, it(acc, head))
                }
            }
        }
        check(operands)
    }.sumOf { (result) -> result }

    part1 { calc(Long::plus, Long::times) }
    part2 { calc(Long::plus, Long::times, { a, b -> "$a$b".toLong() }) }
})

// TODO if time: try backwards?

@AoKSolution
object Day07Inline : PuzDSL({
    infix fun Long.concat(other: Int): Long {
        var mult = 10
        while (mult <= other) mult *= 10
        val conc = times(mult) + other
        return conc
    }

    fun PuzzleInput.calc(concat: Boolean = false) = lines.map {
        val (result, operands) = it.split(": ", limit = 2)
        result.toLong() to operands.splitIntsNotNull()
    }.filter { (result, operands) ->
        fun check(rem: List<Int>, acc: Long = 0L): Boolean {
            val head = rem.first()
            val tail = rem.drop(1)
            return when {
                tail.isNotEmpty() -> check(tail, acc + head)
                    || check(tail, acc * head)
                    || concat && check(tail, acc concat head)

                else -> result == acc + head
                    || result == acc * head
                    || concat && result == acc concat head
            }
        }
        check(operands)
    }.sumOf { (result) -> result }

    part1 { calc() }
    part2 { calc(concat = true) }
})

@AoKSolution
object Day07Retrograde : PuzDSL({
    fun LongArray.solve(target: Long, ofs: Int = lastIndex, concat: Boolean = false): Boolean {
        fun recurse(next: Long) = solve(next, ofs - 1, concat)
        val operand = get(ofs)
        if (ofs == 0) return target == operand
        if (target >= operand && recurse(target - operand)) return true
        if (target % operand == 0L && recurse(target / operand)) return true
        if (concat && target > operand) {
            var left = target
            var right = operand
            while (right != 0L && left % 10 == right % 10) {
                left /= 10
                right /= 10
            }
            if (right == 0L && recurse(left)) return true
        }
        return false
    }

    fun PuzzleInput.calc(concat: Boolean = false) = lines.sumOf {
        val (result, operands) = it.split(": ", limit = 2)
        val values = operands.split(' ').map(String::toLong).toLongArray()
        val target = result.toLong()

        if (values.solve(target, concat = concat)) target else 0L
    }

    part1 { calc() }
    part2 { calc(concat = true) }
})

fun main() = queryDay(7)
    .checkAll(3245122495150L, 105517128211543L)
    .warmup()
    .solveAll(30)

// year 2024 day 7 part 1
//	 Retrograde took 311.333us 👑: 3245122495150
//	 Inline took 3.963917ms (12.73x): 3245122495150
//	 Default took 11.533875ms (37.05x): 3245122495150
//year 2024 day 7 part 2
//	 Retrograde took 491.291us 👑: 105517128211543
//	 Inline took 68.760833ms (139.96x): 105517128211543
//	 Default took 490.298584ms (997.98x): 105517128211543

//Warming up 3 puzzles for 10s each for year 2024 day 7...
//	Default warmed up with 23 iterations
//	Inline warmed up with 137 iterations
//	Retrograde warmed up with 17856 iterations
//year 2024 day 7 part 1
//	 Retrograde took 299us 👑: 3245122495150
//	 Inline took 3.735334ms (12.49x): 3245122495150
//	 Default took 11.772625ms (39.37x): 3245122495150
//year 2024 day 7 part 2
//	 Retrograde took 520.75us 👑: 105517128211543
//	 Inline took 68.935584ms (132.38x): 105517128211543
//	 Default took 432.538625ms (830.61x): 105517128211543