package year2022

import aok.PuzzleInput
import aok.Warmup
import aok.lines
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds

fun main(): Unit = solveDay(
    day = 20,
    warmup = Warmup.eachFor(10.seconds),
    runs = 3,
)

@AoKSolution
object Day20 {

    context(_: PuzzleInput)
    fun part1() = parse().decryptCoordinates()

    context(_: PuzzleInput)
    fun part2() = parse().decryptCoordinates(811589153, 10)

    context(_: PuzzleInput)
    private fun parse() = lines.map(String::toInt).map(::Node).also {
        it.first().left = it.last()
        it.last().right = it.first()
        it.zipWithNext { a, b ->
            a.right = b
            b.left = a
        }
    }

    private fun List<Node>.decryptCoordinates(decryptionKey: Long = 1, rounds: Int = 1): Long {
        val zero = mix(decryptionKey, rounds)
        return generateSequence(zero, Node::right).map(Node::value)
            .filterIndexed { index, _ -> index % 1000 == 0 }
            .drop(1).take(3).sum() * decryptionKey
    }

    private fun List<Node>.mix(decryptionKey: Long = 1, rounds: Int = 1): Node {
        lateinit var zero: Node
        val w = lastIndex / 2
        val modKey = decryptionKey.mod(lastIndex)
        repeat(rounds) {
            for (cur in this) {
                if (cur.value == 0) {
                    zero = cur
                } else {
                    // ab mod n == (a mod n)(b mod n) mod n
                    val moves = (modKey * cur.value.mod(lastIndex)).mod(lastIndex)
                    if (moves > 0) {
                        val dest = cur.traverse(if (moves < w) moves else moves - size)
                        cur moveTo dest
                    }
                }
            }
        }
        return zero
    }

    private class Node(val value: Int) {
        lateinit var left: Node
        lateinit var right: Node
    }

    private fun Node.traverse(n: Int): Node {
        var node = this
        if (n > 0) repeat(n) { node = node.right }
        else repeat(-n) { node = node.left }
        return node
    }

    private infix fun Node.moveTo(dest: Node) {
        // unlink current
        left.right = right
        right.left = left

        // link at dest
        left = dest
        right = dest.right
        left.right = this
        right.left = this
    }
}

@AoKSolution
object Day20Array {
    context(_: PuzzleInput)
    fun part1() = parse().decryptCoordinates()

    context(_: PuzzleInput)
    fun part2() = parse().decryptCoordinates(811589153, 10)

    context(_: PuzzleInput)
    private fun parse() = lines.map(String::toInt).toIntArray()

    private fun IntArray.decryptCoordinates(decryptionKey: Long = 1, rounds: Int = 1): Long {
        val index = mix(decryptionKey, rounds)
        val ofs = index.indexOf(indexOfFirst { it == 0 })
        return decryptionKey * (get(index[(ofs + 1000) % index.size])
                + get(index[(ofs + 2000) % index.size])
                + get(index[(ofs + 3000) % index.size]))
    }

    private fun IntArray.mix(decryptionKey: Long, rounds: Int): IntArray {
        val modKey = decryptionKey.mod(lastIndex)
        val index = IntArray(size) { it }
        repeat(rounds) {
            for ((idx, v) in this.withIndex()) {
                val from = index.indexOf(idx)
                val to = (from + modKey * v.mod(lastIndex)).mod(lastIndex)
                if (from < to) index.copyInto(index, from, from + 1, to + 1)
                else if (from > to) index.copyInto(index, to + 1, to, from)
                index[to] = idx
            }
        }
        return index
    }
}
