package year2024

import aok.Parser
import aok.PuzzleInput
import aok.checkAll
import aok.solveAll
import aok.warmupEach
import aoksp.AoKSolution
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day23 {
    context(PuzzleInput) fun part1() = parse { network ->
        var count = 0
        for ((first, nodes) in network) {
            for (i in 0..<nodes.lastIndex) for (j in i + 1..nodes.lastIndex) {
                val na = nodes[i]
                val nb = nodes[j]
                if (nb !in network[na].orEmpty()) continue
                if (first[0] == 't' || na[0] == 't' || nb[0] == 't') count++
            }
        }
        count
    }

    context(PuzzleInput) fun part2() = parse { network ->
        var best = emptyList<String>()

        for ((first, nodes) in network) {
            // we can't beat the best if we don't have enough nodes
            if (nodes.size < best.lastIndex) continue

            // if we've used more than half the nodes in a lan, we won't beat it
            val bestThreshold = (nodes.size / 2) + 1

            val splits = ArrayDeque(listOf(0))
            while (splits.isNotEmpty()) {
                val split = splits.removeFirst()
                val lan = ArrayDeque<String>(nodes.size + 1)

                // walk back from split, adding in connected nodes
                for (idx in split downTo 0) {
                    val next = nodes[idx]
                    val connected = lan.all { it in network[next].orEmpty() }
                    if (connected) lan.addFirst(next)
                }

                // walk forward from split, adding in connected nodes
                for (idx in split + 1..nodes.lastIndex) {
                    val next = nodes[idx]
                    val connected = lan.all { next in network[it].orEmpty() }
                    if (connected) lan.addLast(next)
                    else splits += idx
                }

                lan.addFirst(first) // don't forget to add the source node
                if (lan.size > best.size) best = lan
                if (lan.size > bestThreshold) splits.clear()
            }
        }

        best.joinToString(",")
    }

    val parse = Parser {
        buildMap<_, ArrayList<String>>(lines.size) {
            for (line in lines) {
                var a = line.substringBefore("-")
                var b = line.substringAfter("-")
                if (b < a) a.let { a = b; b = it }
                getOrPut(a) { ArrayList(20) } += b
            }
            values.forEach { it.sort() }
        }
    }
}

@AoKSolution
object Day23BitSet {
    private val santa = "ta".toInt(36).."tz".toInt(36)

    context(PuzzleInput) fun part1() = parse { network ->
        var count = 0

        network.visitNodes { nodeA, nodes ->
            val santaA = nodeA in santa
            for (other in 0..<nodes.lastIndex) {
                val nodeB = nodes[other]
                val srcB = nodeB shl 12
                val santaB = santaA || nodeB in santa
                for (j in other + 1..nodes.lastIndex) {
                    val nodeC = nodes[j]
                    if ((santaB || nodeC in santa) && network.get(srcB or nodeC)) count++
                }
            }
        }

        count
    }

    context(PuzzleInput) fun part2() = parse { network ->
        var best = emptyList<Int>()

        network.visitNodes { first, nodes ->
            // we can't beat the best if we don't have enough nodes
            if (nodes.lastIndex < best.size) return@visitNodes // continue

            // if we've used more than half the nodes in a lan, we won't beat it
            val bestThreshold = (nodes.size / 2) + 1

            val splits = mutableSetOf(0)
            while (splits.isNotEmpty()) {
                val split = splits.first().also { splits.remove(it) }
                val lan = ArrayDeque<Int>(nodes.size + 1)

                // walk back from split, adding in connected nodes
                for (idx in split downTo 0) {
                    val next = nodes[idx]
                    if (lan.all { network.get(next shl 12 or it) }) lan.addFirst(next)
                }

                // walk forward from split, adding in connected nodes
                for (idx in split + 1..nodes.lastIndex) {
                    val next = nodes[idx]
                    if (lan.all { network.get(it shl 12 or next) }) {
                        lan.addLast(next)
                    } else {
                        splits.add(idx)
                    }
                }

                lan.addFirst(first)
                if (lan.size > best.size) best = lan
                if (lan.size > bestThreshold) break
            }
        }

        best.joinToString(",") { it.toString(36) }
    }

    private inline fun BitSet.visitNodes(f: (Int, List<Int>) -> Unit) {
        var idx = nextSetBit(0)
        while (idx != -1) {
            val end = (idx or 0xFFF)
            val first = idx shr 12

            val nodes = ArrayDeque<Int>()
            while (idx in 0..end) {
                nodes += idx and 0xFFF
                idx = nextSetBit(idx + 1)
            }

            f(first, nodes)
        }
    }

    private val parse = Parser {
        BitSet("zz".toInt(36).let { it shl 12 or it }).apply {
            for (line in lines) {
                val a = line.substringBefore("-").toInt(36)
                val b = line.substringAfter("-").toInt(36)
                if (b < a) set(b shl 12 or a)
                else set(a shl 12 or b)
            }
        }
    }
}

fun main(): Unit = queryDay(23).run {
    checkAll(
        input = """
            aa-ab
            aa-ac
            aa-ba
            aa-cb
            ab-ac
            ba-ca
            cb-ca
            cb-ba
            ca-aa
        """.trimIndent(),
        part1 = 0, part2 = "aa,ba,ca,cb"
    )
    checkAll(
        input = """
            kh-tc
            qp-kh
            de-cg
            ka-co
            yn-aq
            qp-ub
            cg-tb
            vc-aq
            tb-ka
            wh-tc
            yn-cg
            kh-ub
            ta-co
            de-co
            tc-td
            tb-wq
            wh-td
            ta-ka
            td-qp
            aq-cg
            wq-ub
            ub-vc
            de-ta
            wq-aq
            wq-vc
            wh-yn
            ka-de
            kh-ta
            co-tc
            wh-qp
            tb-vc
            td-yn
        """.trimIndent(),
        part1 = 7, part2 = "co,de,ka,ta"
    )
    checkAll(1062, "bz,cs,fx,ms,oz,po,sy,uh,uv,vw,xu,zj,zm")
    warmupEach(5.seconds)
    solveAll(30)
}
