package year2023

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(
    5,
//    warmup = aok.Warmup.iterations(5000), runs=5,
//    input = aok.InputProvider.Example
)

@AoKSolution
object Day05 : PuzDSL({
    val parse = parser {
        val sections = input.split("\n\n")
        val seeds = sections.first().split(": ", " ").drop(1).map(String::toLong)
        seeds to sections.drop(1).map {
            val l = it.lines()
            val name = l.first().substringBefore(' ')
            val mapper = Mapper(l.drop(1).map {
                val (dst, src, len) = it.split(" ").map(String::toLong)
                (src..<src + len) to (dst..<dst + len)
            }.sortedBy { (src) -> src.first })
            name to mapper
        }
    }

    part1(parse) { (seeds, mappers) ->
        seeds.minOf {
            mappers.fold(it) { acc, (_, mapper) -> mapper[acc] }
        }
    }

    part2(parse) { (seeds, mappers) ->
        val seedRanges = seeds.chunked(2) { (a, b) -> a..<(a + b) }
        mappers.fold(seedRanges) { acc, (_, mapper) ->
            acc.flatMap { mapper[it] }
        }.minOf { it.first }
    }
}) {
    @JvmInline
    value class Mapper(private val mappings: List<Pair<LongRange, LongRange>>) {
        operator fun get(n: Long) = mappings.firstOrNull { (src) -> n in src }
            ?.let { (src, dst) -> n - src.first + dst.first }
            ?: n

        operator fun get(r: LongRange) = sequence {
            var unmapped = r
            for ((src, dst) in mappings) {
                if (unmapped.first > src.last) continue
                // outside of range
                if (unmapped.last < src.first) {
                    yield(unmapped)
                    return@sequence
                }
                if (unmapped.first < src.first) {
                    yield(unmapped.first..<src.first)
                    unmapped = src.first..unmapped.last
                }

                // ends in range (and starts in range from above)
                if(unmapped.last in src) {
                    val first = unmapped.first - src.first + dst.first
                    val last = unmapped.last - src.first + dst.first
                    yield(first..last)
                    return@sequence
                }
                // starts in range (doesn't end in range)
                if (unmapped.first in src) {
                    val first = unmapped.first - src.first + dst.first
                    val last = dst.last
                    yield(first..last)
                    unmapped = src.last + 1..unmapped.last
                }
            }
            if(!unmapped.isEmpty())yield(unmapped)
        }
    }
}
