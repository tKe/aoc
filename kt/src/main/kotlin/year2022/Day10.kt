package year2022

import aok.InputProvider
import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution
import aok.solveAll
import aok.warmup
import year2022.Day10NoSeq.cpu
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun main() = with(InputProvider) {
    queryDay(10).warmup(15_000).solveAll(runIterations = 5)
}

@AoKSolution
object Day10InitialAttempt : PuzDSL({
    fun PuzzleInput.cpu() = sequence {
        var x = 1
        var cycle = 1
        for (line in lines) {
            when (line) {
                "noop" -> yield(cycle++ to x)
                else -> {
                    yield(cycle++ to x)
                    yield(cycle++ to x)
                    x += line.substring("addx ".length).toInt()
                }
            }
        }
    }

    part1 {
        cpu().take(220).filter { (cycle) -> cycle % 40 == 20 }
            .sumOf { (cycle, x) -> cycle * x }
    }

    part2 {
        buildString((40 + 1) * 6) {
            cpu().forEach { (cycle, x) ->
                val sprite = x - 1..x + 1
                val crtPixel = (cycle - 1) % 40
                append(if (crtPixel in sprite) "⚪️" else "⚫️")
                if (crtPixel == 39) append('\n')
            }
        }
    }
})

@AoKSolution
object Day10NoCycle : PuzDSL({
    fun String.toIntAt(offset: Int = 0, radix: Int = 10) =
        Integer.parseInt(this, offset, length, radix)

    fun PuzzleInput.cpu() = sequence {
        var x = 1
        for (line in lines) {
            when (line) {
                "noop" -> yield(x)
                else -> {
                    yield(x)
                    yield(x)
                    x += line.toIntAt("addx ".length)
                }
            }
        }
    }

    part1 {
        cpu().take(220).mapIndexedNotNull { idx, i ->
            if (idx % 40 == 19) i + idx * i else null
        }.sum()
    }

    part2 {
        buildString((40 + 1) * 6) {
            cpu().forEachIndexed { idx, x ->
                val sprite = x - 1..x + 1
                val crtPixel = idx % 40
                append(if (crtPixel in sprite) "⚪️" else "⚫️")
                if (crtPixel == 39) append('\n')
            }
        }
    }
})

@AoKSolution
object Day10NoSeq : PuzDSL({

    part1 {
        var sum = 0
        cpu { cycle, i ->
            if (cycle % 40 == 20) sum += cycle * i
            if (cycle == 220) return@part1 sum
        }
        error("didn't reach cycle 220")
    }

    part2 {
        buildString((40 + 1) * 6) {
            cpu { cycle, x ->
                append(if ((cycle - 1) % 40 - x in -1..1) "⚪️" else "⚫️")
                if (cycle % 40 == 0) append('\n')
            }
        }
    }
}) {
    private fun String.toIntAt(offset: Int = 0, radix: Int = 10) =
        Integer.parseInt(this, offset, length, radix)

    @OptIn(ExperimentalContracts::class)
    private inline fun PuzzleInput.cpu(process: (Int, Int) -> Unit) {
        contract {
            callsInPlace(process, InvocationKind.AT_LEAST_ONCE)
        }
        var x = 1
        var cycle = 0
        for (line in lines) {
            when (line) {
                "noop" -> process(++cycle, x)
                else -> {
                    process(++cycle, x)
                    process(++cycle, x)
                    x += line.toIntAt("addx ".length)
                }
            }
        }
    }
}
