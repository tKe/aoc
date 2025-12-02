package year2025

import aok.Parser
import aok.PuzDSL
import aok.PuzzleInput
import aok.Warmup
import aok.input
import aoksp.AoKSolution
import utils.splitLongs
import kotlin.math.log10
import kotlin.math.pow
import kotlin.time.Duration.Companion.seconds

//@AoKSolution
object Day02 : PuzDSL({
    part1 {
        input.split(",").sumOf {
            val range = it.split("-").let { (start, end) -> start.toLong()..end.toLong() }
            range.filter {
                val s = "$it"
                val half = s.length / 2
                s.length == half * 2 && s.take(half) == s.drop(half)
            }.sum()
        }
    }

    part2 {
        val pattern = """^(\d+)\1+$""".toRegex()
        input.split(",").sumOf {
            val range = it.split("-").let { (start, end) -> start.toLong()..end.toLong() }
            range.filter { "$it".matches(pattern) }.sum()
        }
    }
})

@AoKSolution
object Day02Regex : PuzDSL({
    context(_: PuzzleInput)
    fun solve(regex: Regex) = input.splitLongs(',', '-')
        .chunked(2) { (start, end) -> (start..end) }
        .sumOf { r -> r.sumOf { if ("$it".matches(regex)) it else 0 } }
    part1 { solve("""(\d+)\1""".toRegex()) }
    part2 { solve("""(\d+)\1+""".toRegex()) }
})

@AoKSolution
object Day02ChunkGeneration : PuzDSL({
    fun Long.digits(): Int = log10(toDouble()).inc().toInt()
    val pow10 = LongArray(12) { 10.0.pow(it).toLong() }

    fun LongRange.sumInvalidIds(maxChunks: Int = endInclusive.digits()): Long {
        val seen = mutableSetOf<Long>()
        var sum = 0L

        fun visitRange(start: Long, end: Long) {
            val len = start.digits()
            for (chunks in 2..maxChunks) {
                if (len % chunks != 0) continue
                val chunkLen = len / chunks
                val chunkShift = pow10[chunkLen]
                val prefixShift = pow10[len - chunkLen]

                val startPrefix = start / prefixShift
                val endPrefix = end / prefixShift
                for (chunk in startPrefix..endPrefix) {
                    var invalid = chunk
                    repeat(chunks - 1) {
                        invalid *= chunkShift
                        invalid += chunk
                    }
                    if (invalid in start..end && seen.add(invalid)) sum += invalid
                }
            }
        }

        var start = start
        while (start < endInclusive) {
            val end = pow10[start.digits()]
            if (end <= endInclusive) visitRange(start, end - 1)
            else visitRange(start, endInclusive)
            start = end
        }
        return sum
    }

    val ranges = Parser {
        input.split(",", "-")
            .chunked(2) { (start, end) -> (start.toLong()..end.toLong()) }
    }

    part1(ranges) { ranges -> ranges.sumOf { it.sumInvalidIds(maxChunks = 2) } }
    part2(ranges) { ranges -> ranges.sumOf { it.sumInvalidIds() } }
})

fun main() = solveDay(2, warmup = Warmup.eachFor(5.seconds), runs=500)
