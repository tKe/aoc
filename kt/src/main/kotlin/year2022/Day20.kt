package year2022

import InputScopeProvider
import aok.PuzzleInput
import aoksp.AoKSolution
import queryPuzzles
import solveAll
import kotlin.math.absoluteValue

fun main(): Unit = with(InputScopeProvider) {
    queryPuzzles { year == 2022 && day == 20 }.solveAll(
        warmupIterations = 25, runIterations = 1
    )
}

@AoKSolution
object Day20 {

    context(PuzzleInput)
    fun part1() = parse().decryptCoordinates()

    context(PuzzleInput)
    fun part2() = parse().decryptCoordinates(811589153, 10)

    context(PuzzleInput)
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

    class Node(val value: Int) {
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
