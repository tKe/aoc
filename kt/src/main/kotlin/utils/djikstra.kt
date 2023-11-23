package utils

import java.util.*

inline fun <T, C: Comparable<C>> dijkstra(
    start: T,
    isEnd: (T) -> Boolean,
    crossinline cost: (T) -> C,
    crossinline choices: suspend SequenceScope<T>.(T) -> Unit
): T {
    val bests = mutableMapOf<T, C>()
    val pending = PriorityQueue<T>(compareBy { cost(it) })
    pending += start
    while(pending.isNotEmpty()) {
        val current = pending.poll()
        if(isEnd(current)) return current
        val opts = sequence { choices(current) }.toList()
        for(next in opts) {
            val c = cost(next)
            if(next !in bests || bests[next]!! > c){
                bests[next] = c
                pending += next
            }
        }
    }
    error("no route")
}