package year2022

import aok.PuzzleInput
import aok.lines
import aoksp.AoKSolution

fun main(): Unit = solveDay(
    day = 23,
//    aok.warmup = aok.Warmup.eachFor(15.seconds)
)

@AoKSolution
object Day23 {

    private data class Elf(val x: Int, val y: Int)

    context(_: PuzzleInput)
    private fun parse() = lines.flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, c -> if (c == '#') Elf(x, y) else null }
    }.toSet()

    context(_: PuzzleInput)
    fun part1() = parse().simulate().elementAt(10).countSpace()

    context(_: PuzzleInput)
    fun part2() = parse().simulate().count() + 1

    private fun Set<Elf>.countSpace(): Int {
        val width = 1 + maxOf { it.x } - minOf { it.x }
        val height = 1 + maxOf { it.y } - minOf { it.y }
        return width * height - size
    }

    private fun Set<Elf>.simulate() = sequence {
        var elves = this@simulate
        var round = 0
        while (true) {
            elves = elves.toMutableSet().apply {
                elves.groupingBy { it.proposeMove(elves, round) }
                    .aggregate { _, _: Elf?, element, first -> if (first) element else null }
                    .filter { (where, who) -> where != null && who != null }
                    .ifEmpty { return@sequence }
                    .forEach { (where, who) ->
                        remove(who!!)
                        add(where!!)
                    }
            }
            yield(elves)
            round++
        }
    }

    private fun Elf.proposeMove(others: Set<Elf>, round: Int): Elf? {
        val n = copy(y = y - 1)
        val e = copy(x = x + 1)
        val s = copy(y = y + 1)
        val w = copy(x = x - 1)

        val nc = n !in others
        val ec = e !in others
        val sc = s !in others
        val wc = w !in others
        val nec = copy(x = x + 1, y = y - 1) !in others
        val sec = copy(x = x + 1, y = y + 1) !in others
        val swc = copy(x = x - 1, y = y + 1) !in others
        val nwc = copy(x = x - 1, y = y - 1) !in others

        if (nc && ec && sc && wc && nec && sec && swc && nwc) return null // no move

        val moves = listOf(
            n.takeIf { nc && nec && nwc },
            s.takeIf { sc && sec && swc },
            w.takeIf { wc && nwc && swc },
            e.takeIf { ec && nec && sec },
        )

        return moves[round % moves.size]
            ?: moves[(round + 1) % moves.size]
            ?: moves[(round + 2) % moves.size]
            ?: moves[(round + 3) % moves.size]
    }


    private fun Set<Elf>.debug(
        yr: IntRange = minOf { it.y }..maxOf { it.y },
        xr: IntRange = minOf { it.x }..maxOf { it.x },
        elf: String = "🧝", space: String = "▪️",
    ) = yr.forEach { y ->
        println(xr.joinToString("") { x ->
            if (Elf(x, y) in this) elf else space
        })
    }
}
