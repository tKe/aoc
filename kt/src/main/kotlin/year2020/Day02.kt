package year2020

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(
    2,
//    input = aok.InputProvider.Example
)

@AoKSolution
object Day02 : PuzDSL({
    data class Policy(val char: Char, val range: IntRange)

    val parser = lineParser {
        val (l, h, c, password) = it.split("-", " ", ": ")
        Policy(c.single(), l.toInt()..h.toInt()) to password
    }

    part1(parser) { policies ->
        policies.count { (policy, password) ->
            password.count(policy.char::equals) in policy.range
        }
    }

    part2(parser) { policies ->
        policies.count { (policy, password) ->
            (password[policy.range.first - 1] == policy.char) != (password[policy.range.last - 1] == policy.char)
        }
    }
})

