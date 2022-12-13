package year2022

import InputScope
import PuzzleDefinition
import solveAll
import year2022.Day10NoSeq.cpu
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.system.exitProcess

fun main() = solveAll<Day10DSL>(
//    "input.txt" to "example.txt",
    warmupIterations = 15_000,
    runIterations = 5
)

sealed class Day10DSL(body: PuzzleDefinition<Int, String>, variant: String? = null) :
    Puz22DSL<Int, String>(10, variant, body)

object Day10InitialAttempt : Day10DSL({
    fun InputScope.cpu() = sequence {
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

object Day10NoCycle : Day10DSL({
    fun String.toIntAt(offset: Int = 0, radix: Int = 10) =
        Integer.parseInt(this, offset, length, radix)
    fun InputScope.cpu() = sequence {
        var x = 1
        for (line in lines) {
            when (line) {
                "noop" -> yield(x)
                else -> {
                    yield(x)
                    yield(x)
                    x += line.toIntAt( "addx ".length)
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

object Day10NoSeq : Day10DSL({

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
    fun String.toIntAt(offset: Int = 0, radix: Int = 10) =
        Integer.parseInt(this, offset, length, radix)
    @OptIn(ExperimentalContracts::class)
    inline fun InputScope.cpu(process: (Int, Int) -> Unit) {
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
                    x += line.toIntAt( "addx ".length)
                }
            }
        }
    }
}

