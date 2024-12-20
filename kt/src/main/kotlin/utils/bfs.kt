package utils

import kotlin.collections.set

inline fun <T> bfs(
    start: T,
    isEnd: T.() -> Boolean = { false },
    crossinline moves: T.() -> Sequence<T>,
): Int = bfs(start, isEnd, noRoute = { error("no route found") }, moves)

inline fun <T> bfs(
    start: T,
    isEnd: T.() -> Boolean = { false },
    noRoute: () -> Int = { error("no route found") },
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
    return noRoute()
}

inline fun <T> bfsRoute(
    start: T,
    isEnd: T.() -> Boolean = { false },
    crossinline moves: T.() -> Sequence<T>,
): List<T> = bfsRoute(start, isEnd, noRoute = { error("no route found") }, moves)

inline fun <T> bfsRoute(
    start: T,
    isEnd: T.() -> Boolean = { false },
    noRoute: () -> List<T> = { error("no route found") },
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
    return noRoute()
}
