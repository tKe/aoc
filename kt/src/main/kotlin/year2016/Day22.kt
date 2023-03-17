package year2016

import aok.PuzDSL
import aok.Warmup
import aoksp.AoKSolution
import utils.bfs
import utils.bfsRoute

@AoKSolution
object Day22 : PuzDSL({

    data class Node(val x: Int, val y: Int, val size: Int, val used: Int) {
        val avail = size - used

        init {
            check(avail >= 0)
        }
    }

    val parseNodes = parser {
        lines.filter { it.startsWith("/dev/grid/") }
            .map {
                val (x, y, size, used) = it.split("-x", "-y", " ", "T ", "%")
                    .mapNotNull(String::toIntOrNull)
                Node(x, y, size, used)
            }
    }

    part1 {
        val nodes = parseNodes()
        nodes.filter { it.used != 0 }
            .sumOf { a ->
                nodes.filterNot(a::equals).count { a.used <= it.avail }
            }
    }

    part2 {
        val nodes = parseNodes().sortedWith(compareBy(Node::y, Node::x))
        val maxX = nodes.maxOf(Node::x)
        val width = maxX + 1

        val minSize = nodes.minOf(Node::size)
        val walls = nodes.indices.filter { nodes[it].used > minSize }.toSet()

        fun Int.neighbours() = sequence {
            val col = rem(width)
            if (col != 0) yield(dec())
            if (col != maxX) yield(inc())
            yield(plus(width))
            yield(minus(width))
        }.filter { it in nodes.indices && it !in walls }

        bfsRoute(maxX, 0::equals, Int::neighbours).zipWithNext()
            .fold(nodes.indexOfFirst { it.used == 0 } to 0) { (gap, steps), (data, dest) ->
                val gapSteps = bfs(gap, dest::equals) { neighbours().filterNot(data::equals) }
                data to steps + gapSteps + 1
            }.second
    }
})

fun main() = solveDay(
    22,
    warmup = Warmup.iterations(1000), runs = 30
//    input = InputProvider.raw(
//        """
//        Filesystem            Size  Used  Avail  Use%
//        /dev/grid/node-x0-y0   10T    8T     2T   80%
//        /dev/grid/node-x0-y1   11T    6T     5T   54%
//        /dev/grid/node-x0-y2   32T   28T     4T   87%
//        /dev/grid/node-x1-y0    9T    7T     2T   77%
//        /dev/grid/node-x1-y1    8T    0T     8T    0%
//        /dev/grid/node-x1-y2   11T    7T     4T   63%
//        /dev/grid/node-x2-y0   10T    6T     4T   60%
//        /dev/grid/node-x2-y1    9T    8T     1T   88%
//        /dev/grid/node-x2-y2    9T    6T     3T   66%
//    """.trimIndent()
//    )
)
