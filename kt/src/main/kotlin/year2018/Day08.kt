package year2018

import aok.PuzDSL
import aoksp.AoKSolution

fun main(): Unit = solveDay(
        8,
//        input = aok.InputProvider.raw("2 3 0 3 10 11 12 1 1 0 1 99 2 1 1 2"),
)

@AoKSolution
object Day08 : PuzDSL({
    data class Node(val children: List<Node>, val metadata: List<Int>)

    fun Node.traverse(): Sequence<Node> = sequence {
        yield(this@traverse)
        for(child in children) yieldAll(child.traverse())
    }

    val parse = parser {
        val ints = input.splitToSequence(' ').map(String::toInt).iterator()
        DeepRecursiveFunction<() -> Int, Node> { read ->
            val childCount = read()
            val metadataCount = read()
            val children = buildList(childCount) { repeat(childCount) { add(callRecursive(read)) } }
            val metadata = buildList(metadataCount) { repeat(metadataCount) { add(read()) } }
            Node(children, metadata)
        }(ints::next).also {
            require(!ints.hasNext())
        }
    }

    fun Node.dump(prefix: String = "") {
        println("$prefix- metadata: $metadata")
        if (children.isNotEmpty()) {
            println("$prefix  children:")
            children.forEach { it.dump("$prefix  ") }
        }
    }

    part1(parse) { tree ->
        tree.traverse().sumOf { it.metadata.sum() }
    }

    part2(parse) { tree ->
        fun Node.value(): Int = if(children.isEmpty()) metadata.sum()
            else metadata.sumOf { children.getOrNull(it - 1)?.value() ?: 0 }
        tree.value()
    }
})
