package year2023

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

fun main() = solveDay(
    23,
//    input = aok.InputProvider.Example,
)

@AoKSolution
object Day23 : PuzDSL({
    part1(Forest::parse) { forest -> forest.hike(slippery = true) }
    part2(Forest::parse) { forest -> forest.hike(slippery = false) }
}) {
    data class Int2(val x: Int, val y: Int)
    class Forest(private val rows: List<String>) {
        private val entrance by lazy { Int2(rows.first().indexOf('.'), y = 0) }
        private val exit by lazy { Int2(rows.last().indexOf('.'), y = rows.lastIndex) }
        val neighbours = buildMap {
            put(entrance, setOf(Int2(entrance.x, entrance.y + 1) to true))
            for (y in 1..<rows.lastIndex) {
                val (r0, r1, r2) = rows.subList(y - 1, y + 2)
                for (x in 1..<rows.first().lastIndex) if (r1[x] != '#') {
                    put(Int2(x, y), buildSet {
                        if (r0[x] != '#') add(Int2(x, y - 1) to (r0[x] != 'v'))
                        if (r1[x - 1] != '#') add(Int2(x - 1, y) to (r1[x - 1] != '>'))
                        if (r1[x + 1] != '#') add(Int2(x + 1, y) to (r1[x + 1] != '<'))
                        if (r2[x] != '#') add(Int2(x, y + 1) to (r2[x] != '^'))
                    })
                }
            }
        }

        private fun paths(slippery: Boolean = true) = buildMap<Int2, Map<Int2, Int>> {
            val junctions = neighbours.filterValues { it.size > 2 }.keys
            fun searchPath(a: Int2, b: Int2) {
                val pending = ArrayDeque(listOf(a to 0))
                val visited = mutableSetOf<Int2>()
                while (pending.isNotEmpty()) {
                    val (next, dist) = pending.removeFirst()
                    for ((neighbour, navigable) in neighbours[next] ?: continue)
                        if ((!slippery || navigable) && visited.add(neighbour)) {
                            if (neighbour == b) {
                                merge(a, mapOf(b to dist + 1)) { x, y -> x + y }
                                return
                            } else if (neighbour !in junctions) {
                                pending += neighbour to dist + 1
                            }
                        }
                }
            }
            for (j in junctions) {
                searchPath(entrance, j)
                searchPath(j, exit)
                for (d in junctions - j) searchPath(j, d)
            }
        }

        fun hike(slippery: Boolean): Int {
            var max = 0
            val paths = paths(slippery)

            data class State(val at: Int2, val dist: Int, val visited: Set<Int2>)

            val pending = ArrayDeque(listOf(iterator { yield(State(entrance, 0, setOf())) }))
            while (true) {
                while (pending.firstOrNull()?.hasNext() == false) pending.removeFirst()
                val (at, dist, visited) = pending.firstOrNull()?.next() ?: break
                when (at) {
                    exit -> max = maxOf(dist, max)
                    else -> ((paths[at] ?: continue) - visited)
                        .map { (to, toDist) -> State(to, dist + toDist, visited + at) }
                        .let { if (it.isNotEmpty()) pending.addFirst(it.iterator()) }
                }
            }
            return max
        }

        companion object {
            context(PuzzleInput)
            fun parse() = Forest(lines)
        }
    }
}
