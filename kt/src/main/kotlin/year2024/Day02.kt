package year2024

import aok.PuzDSL
import aok.Warmup
import aoksp.AoKSolution
import utils.splitIntsNotNull
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day02 : PuzDSL({
    fun acceptableReport(report: List<Int>): Boolean {
        val deltas = report.zipWithNext { a, b -> b - a }
        return when (deltas.first().sign) {
            1 -> deltas.all { it in 1..3 }
            else -> deltas.all { it in -3..-1 }
        }
    }

    part1 {
        lines.map(String::splitIntsNotNull).count(::acceptableReport)
    }

    part2 {
        lines.map(String::splitIntsNotNull).count { report ->
            report.indices.any { index ->
                val dampened =
                    report.subList(0, index) + report.subList(index + 1, report.size)
                acceptableReport(dampened)
            }
        }
    }
})

fun main() = solveDay(2, warmup = Warmup.eachFor(3.seconds), runs = 50)
