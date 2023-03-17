package year2017

import aok.PuzDSL
import aok.Warmup
import aoksp.AoKSolution
import kotlin.math.absoluteValue

@AoKSolution
object Day11 : PuzDSL({
    /**
     * use a nw x sw coordinate system
     */
    data class HexCoord(val nw: Int = 0, val sw: Int = 0) {
        val length get() = maxOf((sw + nw).absoluteValue, sw.absoluteValue, nw.absoluteValue)
        fun moveBy(nw: Int = 0, sw: Int = 0) = HexCoord(this.nw + nw, this.sw + sw)

        fun move(direction: String) = when (direction) {
            "n" -> moveBy(nw = 1, sw = -1) // ne+nw == n
            "nw" -> moveBy(nw = 1)
            "ne" -> moveBy(sw = -1)
            "s" -> moveBy(nw = -1, sw = 1) // se+sw == s
            "sw" -> moveBy(sw = 1)
            "se" -> moveBy(nw = -1)
            else -> error("invalid direction '$direction'")
        }
    }

    part1 {
        input.trim().split(',')
            .fold(HexCoord(), HexCoord::move)
            .length
    }
    part2 {
        input.trim().split(',')
            .runningFold(HexCoord(), HexCoord::move)
            .maxOf(HexCoord::length)
    }
})


fun main(): Unit = solveDay(
    11,
    warmup = Warmup.iterations(5000), runs = 30
)
