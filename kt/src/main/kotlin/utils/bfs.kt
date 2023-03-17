package utils

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

inline fun <T> bfsRoute(
    start: T,
    isEnd: T.() -> Boolean = { false },
    crossinline moves: T.() -> Sequence<T>,
): List<T> {
    if(start.isEnd()) return emptyList()
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
