package year2017

import aok.PuzDSL
import aok.Warmup
import aoksp.AoKSolution
import kotlin.math.absoluteValue

@AoKSolution
object Day12 : PuzDSL({
    val parsePaths = parser {
        lines.associate {
            val (from, paths) = it.split(" <-> ")
            from to paths.split(", ").toSet()
        }
    }

    fun Map<String, Set<String>>.findGroup(start: String) = buildSet {
        val pending = ArrayDeque(listOf(start))
        while(pending.isNotEmpty()) pending += getValue(pending.removeFirst()).filter(this::add)
    }

    part1 {
        parsePaths().findGroup("0").size
    }


    part2 {
        generateSequence(parsePaths()) { remaining ->
            (remaining - remaining.findGroup(remaining.keys.first())).takeUnless { it.isEmpty() }
        }.count()
    }
})


fun main(): Unit = solveDay(
    12,
//    warmup = Warmup.iterations(5000), runs = 30
)
