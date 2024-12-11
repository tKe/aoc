package year2024

import aok.PuzDSL
import aok.Warmup
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day03Regex : PuzDSL({
    fun MatchResult.mul() = destructured.let { (x, y) -> x.toInt() * y.toInt() }

    part1 {
        """mul\((\d{1,3}),(\d{1,3})\)""".toRegex().findAll(input).sumOf(MatchResult::mul)
    }

    part2 {
        """do\(\)|don't\(\)|mul\((\d{1,3}),(\d{1,3})\)""".toRegex().findAll(input)
            .fold(0 to true) { (sum, enabled), match ->
                when (match.value.substringBefore('(')) {
                    "do" -> sum to true
                    "don't" -> sum to false
                    "mul" -> (if (enabled) sum + match.mul() else sum) to enabled
                    else -> sum to enabled
                }
            }.first
    }
})

@AoKSolution
object Day03Split : PuzDSL({
    val mulRegex = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()
    fun MatchResult.mul() = destructured.let { (x, y) -> x.toInt() * y.toInt() }
    fun String.calc() = mulRegex.findAll(this).sumOf(MatchResult::mul)

    part1 { input.calc() }

    part2 {
        input.splitToSequence("do()")
            .sumOf { it.substringBefore("don't()").calc() }
    }
})

@AoKSolution
object Day03Limit : PuzDSL({
    fun String.indexAfter(needle: String, startIndex: Int = 0) =
        indexOf(needle, startIndex).let { if (it == -1) it else it + needle.length }

    fun String.calc(startIndex: Int = 0, endIndex: Int = length): Int {
        var i = startIndex
        fun readInt(expect: Char): Int? {
            var arg: Int
            if (this[i].isDigit()) arg = this[i++].digitToInt() else return null
            if (this[i].isDigit()) arg = arg * 10 + this[i++].digitToInt()
            if (this[i].isDigit()) arg = arg * 10 + this[i++].digitToInt()
            return if (this[i++] == expect) arg else null
        }

        var sum = 0
        while(i in startIndex..<endIndex) {
            i = indexAfter("mul(", i)
            if(i !in startIndex..<endIndex) break
            val x = readInt(',') ?: continue
            val y = readInt(')') ?: continue
            sum += x * y
        }
        return sum
    }


    part1 { input.calc() }
    part2 {
        var i = 0
        var sum = 0
        while (i >= 0) {
            val lim = input.indexOf("don't()", i).let { if(it == -1) input.length else it }
            sum += input.calc(i, lim)
            i = input.indexAfter("do()", lim)
        }
        sum
    }
})

@AoKSolution
object Day03State : PuzDSL({
    fun calculate(input: String, disabling: Boolean = false): Int {
        var i = 0

        fun op(op: String) = i >= op.length && input.regionMatches(i - op.length, op, 0, op.length)
        fun readInt(expect: Char): Int? {
            var arg: Int
            if (input[i].isDigit()) arg = input[i++].digitToInt() else return null
            if (input[i].isDigit()) arg = arg * 10 + input[i++].digitToInt()
            if (input[i].isDigit()) arg = arg * 10 + input[i++].digitToInt()
            return if (input[i++] == expect) arg else null
        }

        var sum = 0
        while (i in input.indices) {
            i = input.indexOf('(', i + 1)
            when {
                i !in 0..<input.lastIndex -> break

                op("mul") && input[++i].isDigit() -> {
                    val x = readInt(',') ?: continue
                    val y = readInt(')') ?: continue
                    sum += x * y
                }

                disabling && op("don't") && input[++i] == ')' ->
                    i = input.indexOf("do()", i) + 4
            }
        }
        return sum
    }

    part1 { calculate(input) }
    part2 { calculate(input, true) }
})

fun main() {
    solveDay(
        3,
        warmup = Warmup.auto(), runs = 2000,
    )
}
//p1: 178886550
//p2: 87163705