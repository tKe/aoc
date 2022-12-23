package year2022

import InputScopeProvider
import aok.PuzzleInput
import aoksp.AoKSolution
import queryPuzzles
import solveAll

fun main(): Unit = with(InputScopeProvider.Example) {
    queryPuzzles { year == 2022 && day == 23 }
        .solveAll(runIterations = 1)
}

@AoKSolution
object Day23 {

    private data class Elf(val x: Int, val y: Int)

    context(PuzzleInput)
    private fun parse() = lines.flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, c -> if (c == '#') Elf(x, y) else null }
    }.toSet()

    context(PuzzleInput)
    fun part1() = parse().simulate().drop(10).first().countSpace()

    context(PuzzleInput)
    fun part2() = parse().simulate().count()

    private fun Set<Elf>.countSpace(): Int {
        val width = 1 + maxOf { it.x } - minOf { it.x }
        val height = 1 + maxOf { it.y } - minOf { it.y }
        return width * height - size
    }

    private fun Set<Elf>.simulate() = sequence {
        var elves = this@simulate
        var round = 0
        yield(elves)
        while (true) {
            val moved = mutableSetOf<Elf>()
            val new = mutableSetOf<Elf>()

            elves.groupingBy { it.proposeMove(elves, round) }
                .aggregate { _, _: Elf?, element, first -> if (first) element else null }
                .onEach { (where, who) ->
                    if (where != null && who != null) {
                        moved.add(who)
                        new.add(where)
                    }
                }.count()

            if (moved.isEmpty()) {
                break
            }
            elves = (elves - moved) + new
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
        elf: String = "#", space: String = ".",
    ) = yr.forEach { y ->
        println(xr.joinToString("") { x ->
            if (Elf(x, y) in this) elf else space
        })
    }
}

