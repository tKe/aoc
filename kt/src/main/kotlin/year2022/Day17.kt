package year2022

import InputScopeProvider
import aok.PuzzleInput
import aoksp.AoKSolution
import queryPuzzles
import solveAll
import java.nio.ByteBuffer
import kotlin.experimental.and
import kotlin.experimental.or

fun main(): Unit = with(InputScopeProvider) {
    queryPuzzles { year == 2022 && day == 17 }.solveAll(
        warmupIterations = 3000, runIterations = 15
    )
}

@Suppress("NonAsciiCharacters", "unused", "EnumEntryName")
private enum class RockForm(private vararg val lines: Byte) {
    `▂`(0b11110),
    `+`(0b01000, 0b11100, 0b01000),
    `▟`(0b11100, 0b00100, 0b00100),
    `▎`(0b10000, 0b10000, 0b10000, 0b10000),
    `▖`(0b11000, 0b11000);

    fun spawn() = lines.clone()

    companion object : Sequence<RockForm> by Sequence(values()::cycle)
}

private fun <T> Array<T>.cycle() = iterator { while (true) yieldAll(iterator()) }
private fun IntArray.cycle(): IntIterator = object : IntIterator() {
    var current = this@cycle.iterator()
    override fun hasNext() = true
    override fun nextInt(): Int {
        if (!current.hasNext()) current = this@cycle.iterator()
        return current.nextInt()
    }
}

private fun IntIterator.onEach(block: (Int) -> Unit): IntIterator = object : IntIterator() {
    override fun hasNext() = this@onEach.hasNext()
    override fun nextInt() = this@onEach.nextInt().also(block)
}

@AoKSolution
object Day17 {
    context (PuzzleInput)
    private fun parseGas() = lines.single().let {
        IntArray(it.length) { i ->
            when (it[i]) {
                '<' -> -1
                '>' -> 1
                else -> error("excuse you?")
            }
        }
    }

    context(PuzzleInput)
    fun part1() = simulateRockFalls(parseGas().cycle(), 2022).height

    context(PuzzleInput)
    fun part2(rockCount: Long = 1_000_000_000_000): Long {
        val gas = parseGas()
        val (chamber, cycle) = gas.detectCycle()

        val remaining = rockCount - cycle.first
        val cycleRocks = cycle.last - cycle.first
        val cyclesNeeded = remaining / cycleRocks
        val remainingRocks = (remaining % cycleRocks).toInt()

        val baseGas = gas.cycle()
        val base = simulateRockFalls(baseGas, cycle.first)
        val extras = base.simulateRockFalls(baseGas, remainingRocks)

        return extras.height + cyclesNeeded * (chamber.height - base.height)
    }

    private fun simulateRockFalls(gas: IntIterator, rocks: Int) =
        ByteArray(0).simulateRockFalls(gas, rocks)

    private fun ByteArray.simulateRockFalls(gas: IntIterator, rocks: Int) =
        RockForm.take(rocks).fold(clone()) { chamber, form ->
            chamber.simulateRockFall(gas, form)
        }

    private fun IntArray.detectCycle(): Pair<ByteArray, IntRange> {
        var gasConsumed = 0
        val gasMeter = cycle().onEach { gasConsumed++ }

        val lastSeen = mutableMapOf<Pair<Int, Long>, Int>()
        var rocks = 0
        var chamber = ByteArray(0)
        while (true) {
            for (form in RockForm.values()) {
                chamber = chamber.simulateRockFall(gasMeter, form)
                rocks++
            }

            // use the top of the chamber as a Long, combined with the gas offset
            // if we see it again, we've the same top and the same inputs
            if (chamber.height < Long.SIZE_BYTES) continue
            val gasOffset = gasConsumed % size
            val chamberTop = ByteBuffer.wrap(chamber, chamber.height - Long.SIZE_BYTES, Long.SIZE_BYTES).long
            when (val key = gasOffset to chamberTop) {
                !in lastSeen -> lastSeen[key] = rocks
                else -> return chamber to lastSeen[key]!!..rocks
            }
        }
    }

    private fun ByteArray.simulateRockFall(gas: IntIterator, rockForm: RockForm): ByteArray {
        val form = rockForm.spawn()

        var height = height + 3
        val chamber = if (height + form.size > size)
            copyOf(height + form.size) else this

        while (height >= 0) {
            if (gas.hasNext()) {
                val direction = gas.nextInt()
                form push direction
                if (form.collidesWith(chamber, height))
                    form push -direction
            }
            when {
                height == 0 -> break
                form.collidesWith(chamber, height - 1) -> break
                else -> height--
            }
        }

        chamber.merge(form, height)
        return chamber
    }

    private infix fun ByteArray.push(amount: Int) {
        when (amount) {
            -1 -> if (none { it >= 64 }) forEachIndexed { i, b ->
                set(i, (b.toInt() shl 1).toByte())
            }

            1 -> if (none { it and 1 > 0 }) forEachIndexed { i, b ->
                set(i, (b.toInt() ushr 1).toByte())
            }

            else -> error("Unknown direction '$amount'")
        }
    }

    private fun ByteArray.merge(form: ByteArray, offset: Int = 0) =
        form.forEachIndexed { i, b -> set(i + offset, b or this[i + offset]) }

    private fun ByteArray.collidesWith(target: ByteArray, offset: Int = 0) =
        indices.any { get(it) and target[it + offset] > 0 }

    private val ByteArray.height get() = indexOfLast { it > 0 } + 1

}

