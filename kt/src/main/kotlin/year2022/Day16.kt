package year2022

import aok.PuzzleInput
import aok.lines
import aoksp.AoKSolution

fun main() = solveDay(16)

@AoKSolution
object Day16 {

    interface Valve {
        val flowRate: Int
        val tunnels: List<Valve>
    }

    context(_: PuzzleInput)
    private fun startValve() = buildMap<String, Valve> {
        lines.forEach {
            val parts = it.split(
                "Valve ", " has flow rate=",
                "; tunnels lead to valves ", "; tunnel leads to valve ", ", "
            )
            this[parts[1]] = object : Valve {
                override val flowRate = parts[2].toInt()
                override val tunnels by lazy { parts.drop(3).map(this@buildMap::getValue) }
            }
        }
    }.getValue("AA")

    context(_: PuzzleInput)
    fun part1(): Int {
        val start = startValve()
        val routes = routes(start)
        fun Iterable<Valve>.releaseFor(n: Int) = sumOf(Valve::flowRate) * n

        fun process(
            location: Valve,
            opened: Set<Valve> = emptySet(),
            remaining: Int = 30,
            released: Int = 0
        ): Int {
            val maxChild = routes[location].orEmpty()
                .filter { (valve, duration) -> valve !in opened && duration < remaining }
                .maxOfOrNull { (nextLocation, tick) ->
                    process(
                        nextLocation,
                        opened + nextLocation,
                        remaining - tick,
                        released + opened.releaseFor(tick)
                    )
                } ?: 0

            // or stay here...
            val total = released + opened.releaseFor(remaining)
            return maxOf(total, maxChild)
        }
        return process(start)
    }

    context(_: PuzzleInput)
    fun part2(): Int {
        val start = startValve()
        val routes = routes(start)

        fun Iterable<Valve>.releaseFor(n: Int) = sumOf(Valve::flowRate) * n

        val maxRate = routes.keys.releaseFor(1)

        var max = 0
        fun processState(
            me: Pair<Valve, Int>,
            them: Pair<Valve, Int>,
            open: Set<Valve> = emptySet(),
            remaining: Int = 26,
            released: Int = 0,
        ) {
            if (released + remaining * maxRate <= max) return
            fun Pair<Valve, Int>.openValve() = first.takeIf { second == 0 }
            val opened = open + listOfNotNull(me.openValve(), them.openValve())

            fun nextState(mine: Pair<Valve, Int> = me, theirs: Pair<Valve, Int> = them) {
                val (myDest, myEta) = mine
                val (theirDest, theirEta) = theirs
                check(myDest != theirDest) { "can't head to the same place!" }
                val tick = when {
                    myEta == 0 || myEta > theirEta -> theirEta
                    else -> myEta
                }
                if (tick > 0) processState(
                    myDest to maxOf(myEta - tick, 0),
                    theirDest to maxOf(theirEta - tick, 0),
                    opened,
                    remaining - tick,
                    released + opened.releaseFor(tick)
                )
            }

            check(me.second == 0 || them.second == 0) { "shouldn't both be travelling..." }

            fun choices(from: Valve, exclude: Valve? = null) =
                routes[from]!!.filter { (valve, cost) ->
                    valve !in opened && valve != exclude && cost < remaining
                }.ifEmpty { listOf(from to 0) }

            when {
                me.second > 0 -> for (they in choices(them.first, me.first)) {
                    nextState(me, they)
                }

                them.second > 0 -> for (i in choices(me.first, them.first)) {
                    nextState(i, them)
                }

                // neither are travelling
                else -> for (myNext in choices(me.first, them.first)) {
                    for (theirNext in choices(them.first, myNext.first)) {
                        if (theirNext.first != myNext.first) {
                            nextState(myNext, theirNext)
                        }
                    }
                }
            }

            // or stay here...
            val total = released + opened.releaseFor(remaining)
            if (total > max) max = total
        }
        processState(start to 0, start to 0)
        return max
    }

    private fun routes(start: Valve): Map<Valve, List<Pair<Valve, Int>>> {
        val openableValves = start.reachable().filter { it.flowRate > 0 }
        return openableValves
            .associateWith { (openableValves - it).map { other -> other to it.distanceTo(other) + 1 } } +
                (start to (openableValves - start).map { other -> other to start.distanceTo(other) + 1 })
    }

    private fun Valve.reachable(): Set<Valve> {
        val queue = ArrayDeque<Valve>().also { it += this }
        val visited = mutableSetOf(this)
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            for (neighbour in node.tunnels) {
                if (visited.add(neighbour)) queue += neighbour
            }
        }
        return visited
    }

    private fun Valve.distanceTo(other: Valve) = breadthFirstSearch(this, other::equals, Valve::tunnels)
}

private inline fun <T> breadthFirstSearch(start: T, isEnd: (T) -> Boolean, moves: (T) -> Iterable<T>): Int {
    val queue = ArrayDeque(listOf(start to 0))
    val visited = mutableSetOf<T>()
    while (queue.isNotEmpty()) {
        val (node, dist) = queue.removeFirst()
        for (neighbour in moves(node)) {
            if (isEnd(neighbour)) return dist + 1
            if (visited.add(neighbour)) queue += neighbour to dist + 1
        }
    }
    return -1
}
