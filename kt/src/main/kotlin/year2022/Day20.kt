package year2022

import InputScopeProvider
import aok.PuzzleInput
import aoksp.AoKSolution
import queryPuzzles
import solveAll

fun main(): Unit = with(InputScopeProvider) {
    queryPuzzles { year == 2022 && day == 20 }.solveAll(
        warmupIterations = 30, runIterations = 3
    )
}

@AoKSolution
object Day20 {
    context(PuzzleInput)
    private fun parse() = lines.map(String::toInt).let { ints ->
        val end = Node(ints.last())
        ints.asReversed().drop(1).fold(end) { tail, v ->
            Node(v, tail).also {
                it.right = tail
                tail.left = it
            }
        }.also {
            it.left = end
            end.right = it
        }
    }

    class Node(val value: Int, val next: Node? = null) {
        lateinit var left: Node
        lateinit var right: Node
    }

    context(PuzzleInput)
    fun part1() = parse().decryptCoordinates()

    context(PuzzleInput)
    fun part2() = parse().decryptCoordinates(811589153, 10)

    private fun Node.decryptCoordinates(decryptionKey: Long = 1, rounds: Int = 1): Long {
        val zero = mix(decryptionKey, rounds)
        return generateSequence(zero, Node::right).map(Node::value)
            .filterIndexed { index, _ -> index % 1000 == 0 }
            .drop(1).take(3).sum() * decryptionKey
    }

    private fun Node.mix(decryptionKey: Long = 1, rounds: Int = 1): Node {
        lateinit var zero: Node
        val n = generateSequence(this, Node::next).count() - 1 // node is removed during mod
        val keyMod = decryptionKey.mod(n)
        // ab mod n == (a mod n)(b mod n) mod n
        repeat(rounds) {
            var cur = this
            while (true) {
                if (cur.value == 0) {
                    zero = cur
                } else {
                    val moves = (keyMod * cur.value.mod(n)).mod(n)
                    if (moves > 0) {
                        var dest = cur
                        repeat(moves) {
                            dest = dest.right
                        }
                        // unlink current
                        cur.left.right = cur.right
                        cur.right.left = cur.left

                        // link at dest
                        cur.left = dest
                        cur.right = dest.right
                        cur.left.right = cur
                        cur.right.left = cur
                    }
                }
                cur = cur.next ?: break
            }
        }
        return zero
    }
}
