package year2024

import aok.Parser
import aok.PuzzleInput
import aok.checkAll
import aok.solveAll
import aok.warmup
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day13 {
    private operator fun <E> List<E>.component6(): E = this[5]
    val slotMachines = Parser {
        "\\d+".toRegex().findAll(input)
            .mapNotNull { it.value.toLongOrNull() }
            .chunked(6)
    }

    private fun solve(
        ax: Long, ay: Long,
        bx: Long, by: Long,
        x: Long, y: Long,
    ): Long = run {
        val b = (x * ay - y * ax) / (bx * ay - by * ax)
        val a = (x - b * bx) / ax
        if (a * ax + b * bx == x && a * ay + b * by == y) 3 * a + b else 0
    }

    context(PuzzleInput) fun part1() = slotMachines().sumOf { (ax, ay, bx, by, px, py) ->
        solve(ax, ay, bx, by, px + 0, py + 0)
    }

    context(PuzzleInput) fun part2() = slotMachines().sumOf { (ax, ay, bx, by, px, py) ->
        solve(ax, ay, bx, by, px + 10_000_000_000_000, py + 10_000_000_000_000)
    }
}

@AoKSolution
object Day13Scan {
    private fun PuzzleInput.solve(ofs: Long = 0L): Long {
        var s = 0L
        var v = 0
        var r = false
        val arr = LongArray(6)
        for ((i, c) in input.withIndex()) {
            val digit = c.isDigit()
            if (digit) {
                r = true
                arr[v] *= 10L
                arr[v] += c.digitToInt().toLong()
            }
            if ((!digit && r) || i == input.lastIndex) {
                v++
                r = false
                if (v == arr.size) {
                    val (ax, ay, bx, by) = arr
                    val x = arr[4] + ofs
                    val y = arr[5] + ofs
                    s += solve(ax, ay, bx, by, x, y)
                    v = 0
                }
                arr[v] = 0
            }
        }
        return s
    }

    private fun solve(
        ax: Long, ay: Long,
        bx: Long, by: Long,
        x: Long, y: Long,
    ): Long {
        val b = (x * ay - y * ax) / (bx * ay - by * ax)
        val a = (x - b * bx) / ax
        return if (a * ax + b * bx == x && a * ay + b * by == y) 3 * a + b else 0
    }

    context(PuzzleInput) fun part1() = solve()
    context(PuzzleInput) fun part2() = solve(10_000_000_000_000)
}


fun main() {
    queryDay(13)
        .checkAll(
            480L,
            input = """
                Button A: X+94, Y+34
                Button B: X+22, Y+67
                Prize: X=8400, Y=5400
                
                Button A: X+26, Y+66
                Button B: X+67, Y+21
                Prize: X=12748, Y=12176
                
                Button A: X+17, Y+86
                Button B: X+84, Y+37
                Prize: X=7870, Y=6450
                
                Button A: X+69, Y+23
                Button B: X+27, Y+71
                Prize: X=18641, Y=10279
            """.trimIndent()
        )
        .checkAll(38714L, 74015623345775L)
        .warmup(5.seconds)
        .solveAll(3000)
}