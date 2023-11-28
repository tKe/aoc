package year2020

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

fun main() = solveDay(14)

@AoKSolution
object Day14 : PuzDSL({
    fun PuzzleInput.process(block: MutableMap<Long, Long>.(mask: Mask36, addr: Long, value: Long) -> Unit) = buildMap {
        lateinit var mask: Mask36
        for (instr in lines) when {
            instr.startsWith("mask = ") -> mask = Mask36.from(instr.removePrefix("mask = "))
            instr.startsWith("mem[") -> instr.split("[", "] = ").mapNotNull(String::toLongOrNull)
                .also { (addr, value) -> block(mask, addr, value) }
        }
    }.values.sum()

    part1 {
        process { mask, addr, value ->
            put(addr, mask(value))
        }
    }

    part2 {
        process { mask, base, value ->
            for (addr in mask expand base) put(addr, value)
        }
    }
}) {
    data class Mask36(
        val mask: Long, // the mask of X bits
        val bits: Long, // the non-X bits
    ) {
        operator fun invoke(value: Long) = value and mask or bits
        infix fun expand(addr: Long) = bits or addr expand mask
        override fun toString(): String {
            val m = mask.toString(2).padStart(36, '0')
            val b = bits.toString(2).padStart(36, '0')
            return m.zip(b) { x, y -> if (x == '1') 'X' else y }.joinToString("")
        }

        companion object {
            fun from(repr: String) = Mask36(
                repr.replace('1', '0').replace('X', '1').toLong(2),
                repr.replace('X', '0').toLong(2)
            )

        }
    }

    internal infix fun Long.expand(mask: Long) = sequence {
        fun Int.spread(): Long {
            var rem = this // bits to expand out to mask
            var acc = 0L  // current value
            var m = mask
            while (rem > 0 && m > 0) m.takeLowestOneBit().let {
                if (rem and 1 == 1) acc = acc or it
                rem /= 2
                m -= it
            }
            return acc
        }
        repeat(1 shl mask.countOneBits()) {
            yield(and(mask.inv()).or(it.spread()))
        }
    }
}