package year2023

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(
    25,
)

@AoKSolution
object Day25 : PuzDSL({
    val parse = parser {
        lineSeq.flatMap {
            sequence {
                it.split(": ", " ").map { it.toInt(36) }.let {
                    val a = it.take(1).single()
                    it.drop(1).forEach { b ->
                        yield(a to b)
                        yield(b to a)
                    }
                }
            }
        }.groupBy({ it.first }, {it.second })
    }
    part1(parse) {
        val bridges = buildMap<_, Int> {
            fun trackLink(a: Int, b: Int) {
                if(a > b) trackLink(b, a)
                else merge(a to b, 1, Int::plus)
            }
            val visited = mutableSetOf<Int>()
            val pending = ArrayDeque(visited)
            for(start in it.keys) {
                visited.clear()
                pending += start
                while(pending.isNotEmpty()) {
                    val n = pending.removeFirst()
                    pending += it.getValue(n).filter(visited::add).onEach { trackLink(n, it) }
                }
            }
        }.entries.sortedByDescending { it.value }.take(3).map { it.key }

        val split = it.mapValues { (a, bs) ->
            if (bridges.any { (ba, bb) -> ba == a || bb == a }) {
                bs.filter {
                    bridges.none { (ba, bb) -> (ba == a && bb == it ) || (bb == a && ba == it)  }
                }
            } else bs
        }

        val start = split.keys.random()
        val visted = mutableSetOf(start)
        val pending = ArrayDeque(visted)
        while(pending.isNotEmpty()) {
            pending += split.getValue(pending.removeFirst()).filter(visted::add)
        }
        val (ca, cb) = split.keys.partition(visted::contains)
        ca.size * cb.size
    }
})
