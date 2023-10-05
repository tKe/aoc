package year2017

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

@AoKSolution
object Day24 : PuzDSL({
    data class Component(val a: Int, val b: Int)

    fun PuzzleInput.parseComponents() = lines.map {
        it.split("/").map(String::toInt).let { (a, b) -> Component(a, b) }
    }

    data class Bridge(val components: List<Component> = emptyList(), val nextPin: Int = 0) {
        val strength by lazy { components.sumOf { it.a + it.b } }
        val length = components.size
        infix fun accept(next: Component) = next.a == nextPin || next.b == nextPin
        operator fun plus(next: Component) = when (nextPin) {
            next.a -> Bridge(components + next, next.b)
            next.b -> Bridge(components + next, next.a)
            else -> error("incompatible")
        }
    }

    part1 {
        DeepRecursiveFunction<Pair<Bridge, List<Component>>, Int> { (b, c) ->
            c.filter(b::accept).maxOfOrNull {
                callRecursive(b + it to c - it)
            } ?: b.strength
        }(Bridge() to parseComponents())
    }

    part2 {
        DeepRecursiveFunction<Pair<Bridge, List<Component>>, List<Bridge>> { (bridge, components) ->
            components.filter(bridge::accept)
                .flatMap { callRecursive(bridge + it to components - it) }
                .ifEmpty { listOf(bridge) }
                .let { bridges ->
                    val longest = bridges.maxOf { it.length }
                    bridges.filter { it.length == longest }
                }
        }(Bridge() to parseComponents())
            .maxOf { it.strength }
    }
})

fun main(): Unit = solveDay(
    24,
//    input = raw(
//        """
//            0/2
//            2/2
//            2/3
//            3/4
//            3/5
//            0/1
//            10/1
//            9/10
//        """.trimIndent()
//    )
)
