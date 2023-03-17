package year2015

import aok.PuzDSL
import aoksp.AoKSolution


@AoKSolution
object Day14 : PuzDSL({
    val raceDuration = 2503

    data class Reindeer(val name: String, val speed: Int, val stamina: Int, val recovery: Int) {
        val cycleLength = stamina + recovery
    }

    fun String.parseReindeer() = split(
        " can fly ",
        " km/s for ",
        " seconds, but then must rest for ",
        " seconds"
    ).let { (name, speed, stamina, recovery) ->
        Reindeer(name, speed.toInt(), stamina.toInt(), recovery.toInt())
    }

    fun Reindeer.distanceAfter(n: Int): Int {
        val fullCycles = n / cycleLength
        val remainder = n % cycleLength
        return fullCycles * (speed * stamina) + minOf(speed * stamina, remainder * speed)
    }

    part1 {
        lines.map(String::parseReindeer)
            .maxOf { it.distanceAfter(raceDuration) }
    }

    part2 {
        val reindeer = lines.map(String::parseReindeer)
        (1..raceDuration).asSequence()
            .flatMap { elapsed -> reindeer.groupBy { it.distanceAfter(elapsed) }.maxBy { it.key }.value }
            .groupingBy { it }.eachCount().maxBy { it.value }
    }
})

fun main() = solveDay(
    14,
//    input = InputProvider.raw(
//        """
//            Comet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds.
//            Dancer can fly 16 km/s for 11 seconds, but then must rest for 162 seconds.
//        """.trimIndent()
//    )
)
