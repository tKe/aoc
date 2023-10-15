package year2018

import aok.PuzDSL
import aoksp.AoKSolution
import year2018.Day18.Acre.*

fun main(): Unit = solveDay(
    18,
//    warmup = aok.Warmup.iterations(30), runs = 50,
//    input = aok.InputProvider.raw(
//        """
//            .#.#...|#.
//            .....#|##|
//            .|..|...#.
//            ..|#.....#
//            #.#|||#|#|
//            ...#.||...
//            .|....|...
//            ||...#|.#|
//            |.||||..|.
//            ...#.|..|.
//        """.trimIndent()
//    )
)

@AoKSolution
object Day18 : PuzDSL({
    val parse = parser {
        val width = input.indexOfFirst { it !in "#|." }
        val data = input.mapNotNull {
            when (it) {
                '#' -> `🪚`
                '|' -> `🌳`
                '.' -> `🟫`
                else -> null
            }
        }
        LCA(width, data)
    }

    part1(parse) { lca ->
        generateSequence(lca, LCA::evolve).elementAt(10)
            .also(::println).resourceValue
    }

    part2(parse) { lca ->
        val lastSeen = mutableMapOf<LCA, Int>()
        val history = mutableListOf<LCA>()
        val repeating = generateSequence(lca, LCA::evolve).onEach(history::add)
            .withIndex().firstNotNullOf { (idx, lca) ->
                lastSeen.put(lca, idx)?.rangeUntil(idx)
            }
        val idx = repeating.first + (1000000000 - repeating.first) % (repeating.last - repeating.first + 1)
        history[idx].also(::println).resourceValue
    }
}) {
    data class LCA(private val width: Int, private val data: List<Acre>) {
        private val counts by lazy { data.counts() }
        val resourceValue by lazy { counts(`🌳`) * counts(`🪚`) }
        private fun neighbourCounts(idx: Int) = IntArray(Acre.entries.size).also { counts ->
            val firstCol = idx % width == 0
            val lastCol = idx % width == width - 1
            if (!firstCol) {
                data.getOrNull(idx - width - 1)?.ordinal?.let { counts[it]++ }
                counts[data[idx - 1].ordinal]++
                data.getOrNull(idx + width - 1)?.ordinal?.let { counts[it]++ }
            }
            data.getOrNull(idx - width)?.ordinal?.let { counts[it]++ }
            data.getOrNull(idx + width)?.ordinal?.let { counts[it]++ }
            if (!lastCol) {
                data.getOrNull(idx - width + 1)?.ordinal?.let { counts[it]++ }
                counts[data[idx + 1].ordinal]++
                data.getOrNull(idx + width + 1)?.ordinal?.let { counts[it]++ }
            }
        }

        private fun Iterable<Acre>.counts() = IntArray(Acre.entries.size).also {
            for (acre in this) it[acre.ordinal]++
        }

        private operator fun IntArray.invoke(a: Acre) = get(a.ordinal)

        fun evolve() = copy(data = data.mapIndexed { index, acre ->
            val count = neighbourCounts(index)
            when {
                acre == `🟫` && (count(`🌳`) >= 3) -> `🌳`
                acre == `🌳` && (count(`🪚`) >= 3) -> `🪚`
                acre == `🪚` && (count(`🪚`) == 0 || count(`🌳`) == 0) -> `🟫`
                else -> acre
            }
        })

        private val repr by lazy {
            buildString {
                data.chunked(width).forEach { row ->
                    row.joinTo(this, "")
                    appendLine()
                }
            }
        }

        override fun toString() = repr
    }

    @Suppress("EnumEntryName")
    enum class Acre { `🟫`, `🌳`, `🪚` }
}