package year2015

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

@AoKSolution
object Day17 : PuzDSL({
    operator fun Int.contains(index: Int) = (1 shl index) and this != 0
    fun PuzzleInput.containers() = lines.mapNotNull(String::toIntOrNull)
    fun List<Int>.sumSelected(bits: Int) = filterIndexed { idx, _ -> idx in bits }.sum()

    part1 {
        val containers = containers()
        (0 until (1 shl containers.size))
            .count { containers.sumSelected(it) == 150 }
    }

    part2 {
        val containers = containers()
        val containerBits = containers.indices.map { 1 shl it }

        val minContainers = breadthFirstSearch(0, { containers.sumSelected(it) == 150 }) { selected ->
            containerBits.map { it or selected }
        }

        (0 until (1 shl containers.size))
            .filter { it.countOneBits() == minContainers }
            .count { containers.sumSelected(it) == 150 }
    }
})

private inline fun breadthFirstSearch(start: Int, isEnd: (Int) -> Boolean, moves: (Int) -> Iterable<Int>): Int {
    val queue = ArrayDeque(listOf(start to 0))
    val visited = mutableSetOf<Int>()
    while (queue.isNotEmpty()) {
        val (node, dist) = queue.removeFirst()
        for (neighbour in moves(node)) {
            if (isEnd(neighbour)) return dist + 1
            if (visited.add(neighbour)) queue += neighbour to dist + 1
        }
    }
    return -1
}


fun main() = solveDay(
    17,
)
