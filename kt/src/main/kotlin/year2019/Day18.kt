package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import utils.dijkstra

fun main() = solveDay(
    18,
)

@AoKSolution
object Day18 : PuzDSL({
    val vaultParser = parser {
        buildMap {
            lines.forEachIndexed { y, s ->
                s.forEachIndexed { x, c ->
                    when (c) {
                        '.' -> Corridor
                        '@' -> Entrance(Int2(x, y))
                        in 'a'..'z' -> Key(c - 'a')
                        in 'A'..'Z' -> Door(c - 'A')
                        else -> null
                    }?.let { put(Int2(x, y), it) }
                }
            }
        }
    }

    fun Map<Int2, Cell>.directlyReachable(start: Int2): Map<Node, Int> = buildMap {
        val map = this@directlyReachable
        val visited = mutableSetOf(start)
        val pending = ArrayDeque(listOf(start to 0))
        while (pending.isNotEmpty()) {
            val (pos, dist) = pending.removeFirst()
            val opts = pos.neighbours().filter(map::contains).filter(visited::add)
            for (next in opts) when (val node = map.getValue(next)) {
                Corridor -> pending += next to dist + 1
                is Node -> put(node, dist + 1)
            }
        }
    }

    fun Map<Int2, Cell>.distanceMatrix() = buildMap<Node, Map<Node, Int>> {
        for ((pos, node) in this@distanceMatrix)
            if (node is Node) this[node] = directlyReachable(pos)
    }

    fun Map<Node, Map<Node, Int>>.route(): Int {
        val matrix: Map<Node, Map<Node, Int>> = this

        fun reachableKeys(start: Node, locked: IntSet): Map<Key, Int> = buildMap {
            val visited = mutableSetOf(start)
            val pending = ArrayDeque(visited.map { it to 0 })
            while (pending.isNotEmpty()) {
                val (current, cost) = pending.removeFirst()
                for ((next, dist) in matrix.getValue(current)) if (visited.add(next)) when {
                    next is Door && next.id in locked -> {}
                    next is Key && next.id in locked -> put(next, cost + dist)
                    else -> pending += next to cost + dist
                }
            }
        }

        data class State(
            val nodes: List<Node> = keys.filterIsInstance<Entrance>(),
            val locked: IntSet = IntSet(0..<26),
        ) {
            var totalCost: Int = 0
                private set

            fun unlock(idx: Int, key: Key, cost: Int) = State(
                nodes = nodes.mapIndexed { it, old -> if (it == idx) key else old },
                locked = locked - key.id
            ).also { it.totalCost = totalCost + cost }
        }

        return dijkstra(State(), { it.locked.isEmpty() }, State::totalCost) {
            for ((idx, node) in it.nodes.withIndex()) {
                for ((key, dist) in reachableKeys(node, it.locked)) {
                    yield(it.unlock(idx, key, dist))
                }
            }
        }.totalCost
    }

    fun Map<Int2, Cell>.expandEntrances(): Map<Int2, Cell> = buildMap(size) {
        this += this@expandEntrances
        val entrance = this@expandEntrances.filterValues { it is Entrance }.keys.single()
        remove(entrance)
        entrance.neighbours().forEach(this::remove)
        with(entrance) {
            listOf(
                copy(x = x - 1, y = y - 1),
                copy(x = x + 1, y = y + 1),
                copy(x = x - 1, y = y + 1),
                copy(x = x + 1, y = y - 1),
            ).forEach { put(it, Entrance(it)) }
        }
    }

    part1(vaultParser) { vault ->
        vault.distanceMatrix().route()
    }

    part2(vaultParser) { vault ->
        vault.expandEntrances().distanceMatrix().route()
    }
}) {
    data class Int2(val x: Int, val y: Int) {
        fun neighbours() = sequenceOf(copy(x = x + 1), copy(x = x - 1), copy(y = y + 1), copy(y = y - 1))
    }

    sealed interface Cell
    data object Corridor : Cell
    sealed interface Node : Cell
    data class Entrance(val at: Int2) : Node
    data class Door(val id: Int) : Node
    data class Key(val id: Int) : Node

    @JvmInline
    value class IntSet(private val bits: Int = 0) {
        constructor(bits: IntProgression) : this(bits.fold(IntSet(0), IntSet::plus).bits)
        fun isEmpty() = bits == 0
        operator fun contains(element: Int) = (bits and 1.shl(element)) != 0
        operator fun plus(bit: Int) = IntSet(bits or 1.shl(bit))
        operator fun minus(bit: Int) = IntSet(bits and 1.shl(bit).inv())
    }
}

