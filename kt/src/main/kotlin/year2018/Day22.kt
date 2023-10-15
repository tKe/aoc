package year2018

import aok.PuzDSL
import aoksp.AoKSolution
import utils.splitIntsNotNull
import java.util.*

fun main(): Unit = solveDay(
    22,
//    input = aok.InputProvider.raw(
//        """
//        depth: 510
//        target: 10,10
//        """.trimIndent()
//    )
)

@AoKSolution
object Day22 : PuzDSL({

    val parser = parser {
        val (depth, x, y) = input.splitIntsNotNull("\n", ",", " ")
        Cave(depth, Region(x, y))
    }

    part1(parser) { cave ->
        val (tx, ty) = cave.target
        var sum = 0
        for (y in 0..ty) for (x in 0..tx)
            sum += cave[x, y].riskLevel
        sum
    }

    part2(parser) { cave ->
        data class State(val x: Int = 0, val y: Int = 0, val equipped: Tool = Tool.Torch)

        infix fun Tool.usableIn(regionType: RegionType) = this != when (regionType) {
            RegionType.Rocky -> Tool.Nothing
            RegionType.Wet -> Tool.Torch
            RegionType.Narrow -> Tool.ClimbingGear
        }

        fun State.options() = sequence {
            val currentRegion = cave[x, y]

            if (x > 0 && equipped usableIn cave[x - 1, y]) yield(copy(x = x - 1) to 1)
            if (y > 0 && equipped usableIn cave[x, y - 1]) yield(copy(y = y - 1) to 1)
            if (equipped usableIn cave[x + 1, y]) yield(copy(x = x + 1) to 1)
            if (equipped usableIn cave[x, y + 1]) yield(copy(y = y + 1) to 1)

            for (tool in Tool.entries)
                if (equipped != tool && tool usableIn currentRegion)
                    yield(copy(equipped = tool) to 7)
        }

        fun durationToReach(end: State, initial: State = State()): Int {
            val queue = PriorityQueue<Pair<State, Int>>(compareBy { (_, time) -> time })
            queue += initial to 0
            val fastest = mutableMapOf(initial to 0).withDefault { Int.MAX_VALUE }
            while (queue.isNotEmpty()) {
                val (node, time) = queue.poll()
                if (node == end) return time
                if (time > fastest.getValue(node)) continue
                for ((next, dur) in node.options()) {
                    val totalTime = time + dur
                    if ((fastest.getValue(next)) > totalTime) {
                        fastest[next] = totalTime
                        queue += next to totalTime
                    }
                }
            }
            error("no route found")
        }

        durationToReach(State(cave.target.x, cave.target.y, Tool.Torch))
    }
}) {
    data class Region(val x: Int, val y: Int)
    enum class RegionType {
        Rocky, Wet, Narrow;

        val riskLevel = ordinal
    }

    class Cave(
        val depth: Int, val target: Region,
        private val sectionWidth: Int = minOf(target.x, 128),
        private val sectionHeight: Int = minOf(target.y, 128)
    ) {
        private val sections = mutableMapOf<Pair<Int, Int>, Section>()

        private fun sectionFor(x: Int, y: Int): Section {
            val sx = sectionWidth * (x / sectionWidth)
            val sy = sectionHeight * (y / sectionHeight)
            return sections.getOrPut(sx to sy) { Section(sx, sy) }
        }

        operator fun get(x: Int, y: Int) = sectionFor(x, y)[x, y]

        private inner class Section(val x: Int, val y: Int) {
            private val el: IntArray by lazy {
                IntArray(sectionWidth * sectionHeight).also { el ->
                    // if we have the index in our data, use it from the in-progress,
                    // otherwise fall-back to getting another section from the cave system
                    fun get(x: Int, y: Int) = index(x, y)?.let { el[it] }
                        ?: sectionFor(x, y).let { it.el[it.index(x, y) ?: error("wtf")] }

                    var i = 0
                    for (y in y..<y + sectionHeight)
                        for (x in x..<x + sectionWidth)
                            el[i++] = (depth + when {
                                y == 0 && x == 0 || y == target.y && x == target.x -> 0
                                y == 0 -> x * 16807
                                x == 0 -> y * 48271
                                else -> get(x, y - 1) * get(x - 1, y)
                            }) % 20183
                }
            }
            private val rt by lazy {
                Array(sectionWidth * sectionHeight) { RegionType.entries[el[it] % 3] }
            }

            private fun index(x: Int, y: Int): Int? {
                val sx = x - this.x
                val sy = y - this.y
                return if (sx in 0..<sectionWidth && sy in 0..<sectionHeight) sy * sectionWidth + sx
                else null
            }

            operator fun get(x: Int, y: Int): RegionType {
                return index(x, y)?.let { rt[it] } ?: error("invalid coordinates ($x, $y) for this section ($this)")
            }
        }
    }

    enum class Tool { ClimbingGear, Torch, Nothing }
}