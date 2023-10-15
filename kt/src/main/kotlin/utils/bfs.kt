package utils

import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.set

inline fun <T> bfs(
    start: T,
    isEnd: T.() -> Boolean = { false },
    crossinline moves: T.() -> Sequence<T>,
): Int {
    val queue = ArrayDeque(listOf(start to 0))
    val visited = mutableSetOf<T>()
    while (queue.isNotEmpty()) {
        val (node, dist) = queue.removeFirst()
        for (neighbour in node.moves()) {
            if (neighbour.isEnd()) return dist + 1
            if (visited.add(neighbour)) queue += neighbour to dist + 1
        }
    }
    error("no route found")
}

inline fun <T> bfs(
    start: T,
    isEnd: T.() -> Boolean = { false },
    comparator: Comparator<T>,
    crossinline moves: (T) -> Sequence<T>,
): T {
    val queue = PriorityQueue(comparator).also { it += start }
    val visited = mutableSetOf<T>()
    while (queue.isNotEmpty()) {
        val node = queue.poll()
        for (neighbour in moves(node)) {
            if (neighbour.isEnd()) return neighbour
            if (visited.add(neighbour)) queue += neighbour
        }
    }
    error("no route found")
}

inline fun <T> bfsCost(
    start: T,
    isEnd: (T) -> Boolean = { false },
    crossinline moves: (T) -> Sequence<Pair<T, Int>>,
) = bfs(start to 0, { isEnd(first) }, compareBy { (_, cost) -> cost }) { (state, cost) ->
    moves(state).map { (next, delta) -> next to (cost + delta) }
}

inline fun <T> bfsRoute(
    start: T,
    isEnd: T.() -> Boolean = { false },
    crossinline moves: T.() -> Sequence<T>,
): List<T> {
    if (start.isEnd()) return emptyList()
    val queue = ArrayDeque(listOf(start))
    val visited = mutableSetOf(start)
    val cameFrom = mutableMapOf<T, T>()
    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()
        for (neighbour in node.moves()) {
            if (neighbour.isEnd()) return (listOf(neighbour) + generateSequence(node, cameFrom::get)).asReversed()
            if (visited.add(neighbour)) {
                queue += neighbour
                cameFrom[neighbour] = node
            }
        }
    }
    error("no route found")
}
