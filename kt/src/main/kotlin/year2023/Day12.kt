package year2023

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(12, warmup = aok.Warmup.iterations(25), runs = 5)

@AoKSolution
object Day12 : PuzDSL({
    val parser = lineParser { line ->
        val record = line.substringBefore(' ')
        val blocks = line.substringAfter(' ').split(',').map(String::toInt)
        ConditionRecord(record) to blocks
    }

    part1(parser) {
        it.sumOf { (record, check) -> record.arrangements(check) }
    }

    part2(parser) {
        operator fun <T> List<T>.times(n: Int) = List(n) { this }.flatten()
        operator fun ConditionRecord.times(n: Int) = ConditionRecord(List(n) { record }.joinToString("?"))

        it.map { (record, blocks) -> (record * 5) to (blocks * 5) }
            .sumOf { (record, blocks) -> record.arrangements(blocks) }
    }
}) {
    @JvmInline
    value class ConditionRecord(val record: String) {
        override fun toString() = record
        fun arrangements(blocks: List<Int>) =
            CachedDeepRecursiveFunction { (ofs, blockIdx, slack): CacheKey ->
                val blockSize = blocks[blockIdx]
                val moreBlocks = blockIdx != blocks.lastIndex
                var sum = 0L

                for (gap in 0..slack) {
                    val blockStart = ofs + gap
                    val blockEnd = blockStart + blockSize

                    if (anyInRange('#', ofs, blockStart)) break
                    if (anyInRange('.', blockStart, blockEnd)) continue
                    if (moreBlocks && record[blockEnd] == '#') continue

                    sum += when {
                        moreBlocks -> callRecursive(CacheKey(blockEnd + 1, blockIdx + 1, slack - gap))
                        anyInRange('#', blockEnd) -> 0L
                        else -> 1L
                    }
                }
                sum
            }(CacheKey(0, 0, record.length - blocks.sum() - blocks.size + 1))

        private fun anyInRange(char: Char, ofs: Int, endExclusive: Int = record.length) =
            record.indexOf(char, ofs) in ofs..<endExclusive
    }

    data class CacheKey(val ofs: Int, val blk: Int) {
        constructor(ofs: Int, blk: Int, slk: Int) : this(ofs, blk) {
            slack = slk
        }

        private var slack: Int = 0
        operator fun component3() = slack
    }
}

@AoKSolution
object Day12Recurse : PuzDSL({
    val parser = lineParser { line ->
        line.substringBefore(' ') to line.substringAfter(' ').split(',').map(String::toInt)
    }

    fun String.anyInRange(char: Char, ofs: Int, endExclusive: Int = length) = indexOf(char, ofs) in ofs..<endExclusive

    fun Pair<String, List<Int>>.arrangements(
        ofs: Int = 0,
        blockIdx: Int = 0,
        slack: Int = first.length - second.sum() - second.size + 1,
        cache: MutableMap<Pair<Int, Int>, Long> = mutableMapOf(),
    ): Long = cache.getOrPut(ofs to blockIdx) {
        val moreBlocks = blockIdx != second.lastIndex
        val block = second[blockIdx]
        var sum = 0L
        for (gap in 0..slack) {
            val blockStart = ofs + gap
            val blockEnd = blockStart + block

            if (first.anyInRange('#', ofs, blockStart)) break
            if (first.anyInRange('.', blockStart, blockEnd)) continue
            if (first.getOrNull(blockEnd) == '#') continue

            sum += when {
                moreBlocks -> arrangements(blockEnd + 1, blockIdx + 1, slack - gap, cache)
                first.anyInRange('#', blockEnd) -> 0
                else -> 1
            }
        }
        sum
    }

    fun Pair<String, List<Int>>.unfold() = let { (template, constraints) ->
        List(5) { template }.joinToString("?") to List(5) { constraints }.flatten()
    }

    part1(parser) { records ->
        records.sumOf { it.arrangements() }
    }

    part2(parser) { records ->
        records.sumOf { it.unfold().arrangements() }
    }
})

@Suppress("FunctionName")
fun <T, R> CachedDeepRecursiveFunction(block: suspend DeepRecursiveScope<T, R>.(T) -> R) =
    mutableMapOf<T, R>().let { cache -> DeepRecursiveFunction { cache.getOrPut(it) { block(it) } } }
