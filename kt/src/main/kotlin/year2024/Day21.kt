package year2024

import aok.PuzzleInput
import aok.checkAll
import aok.solveAll
import aok.warmup
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day21 {
    context(PuzzleInput) fun part1() = solve(2)
    context(PuzzleInput) fun part2() = solve(25)

    fun PuzzleInput.solve(robots: Int) = run {
        val cache = mutableMapOf<Key, Long>()
        lines.sumOf {
            val num = it.dropLast(1).toInt()
            val cost = "A$it".zipWithNext { a, b -> CODEPAD.navigate(a, b, robots, cache) }.sum()
            num * cost
        }
    }

    private fun Map<Char, Pt>.navigate(
        a: Char,
        b: Char,
        robots: Int,
        cache: MutableMap<Key, Long> = mutableMapOf()
    ) = navigate(getValue(a), getValue(b), getValue(' '), robots, cache)

    @JvmInline
    private value class Key(val k: Int) {
        // pt x/y are only ever 0..2 so only need 2 bits to encapsulate
        constructor(a: Pt, b: Pt, robots: Int) : this(
            robots
                shl 2 or a.x
                shl 2 or a.y
                shl 2 or b.x
                shl 2 or b.y
        )
    }

    private fun navigate(a: Pt, b: Pt, avoid: Pt, robots: Int, cache: MutableMap<Key, Long>): Long {
        val k = Key(a, b, robots)
        cache[k]?.let { return it }

        var min = Long.MAX_VALUE
        val frontier = ArrayDeque<Pair<Pt, String>>()
        frontier.add(a to "A")
        while (frontier.isNotEmpty()) {
            val (at, pressed) = frontier.removeFirst()
            if (at == b) {
                val cost = when (robots) {
                    0 -> pressed.length.toLong()
                    else -> "${pressed}A".zipWithNext { na, nb -> NAVPAD.navigate(na, nb, robots - 1, cache) }.sum()
                }
                min = minOf(min, cost)
            } else if (at != avoid) {
                if (at.x > b.x) frontier.add(Pt(at.x.dec(), at.y) to "$pressed<")
                if (at.x < b.x) frontier.add(Pt(at.x.inc(), at.y) to "$pressed>")
                if (at.y > b.y) frontier.add(Pt(at.x, at.y.dec()) to "$pressed^")
                if (at.y < b.y) frontier.add(Pt(at.x, at.y.inc()) to "${pressed}v")
            }
        }

        cache[k] = min
        return min
    }

    data class Pt(val x: Int, val y: Int)

    val CODEPAD = "789456123 0A".withIndex().associate { (i, c) -> c to Pt(x = i % 3, y = i / 3) }
    val NAVPAD = " ^A<v>".withIndex().associate { (i, c) -> c to Pt(x = i % 3, y = i / 3) }
}

fun main() {
    queryDay(21)
        .checkAll(
            input = """
                029A
                980A
                179A
                456A
                379A
            """.trimIndent(),
            part1 = 126384,
        )
        .checkAll(part1 = 238078, part2 = 293919502998014)
        .warmup(10.seconds)
        .solveAll(30)
}
