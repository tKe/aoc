package year2020

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(
    4,
//    input = aok.InputProvider.Example
)

@AoKSolution
object Day04 : PuzDSL({

    val parser = parser {
        input.split("\n\n").map {
            it.split(" ", "\n").filter(String::isNotBlank).associate {
                val (k, v) = it.split(':')
                k to v
            }
        }
    }

    part1(parser) { passports ->
        val required = setOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
        passports.count { it.keys.containsAll(required) }
    }

    part2(parser) { passports ->
        val validations: Map<String, (String) -> Boolean> = mapOf(
            "byr" to { (it.toIntOrNull() ?: 0) in 1920..2020 },
            "iyr" to { (it.toIntOrNull() ?: 0) in 2010..2020 },
            "eyr" to { (it.toIntOrNull() ?: 0) in 2020..2030 },
            "hgt" to {
                val height = it.takeWhile(Char::isDigit).toIntOrNull() ?: 0
                val unit = it.takeLast(2)
                height in when (unit) {
                    "cm" -> 150..193
                    "in" -> 59..76
                    else -> IntRange.EMPTY
                }
            },
            "hcl" to "#[a-f0-9]{6}".toRegex()::matches,
            "ecl" to setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")::contains,
            "pid" to { it.length == 9 && it.all(Char::isDigit) },
        )
        passports.count {
            validations.all { (k, v) -> it[k]?.let(v) ?: false }
        }
    }
})

