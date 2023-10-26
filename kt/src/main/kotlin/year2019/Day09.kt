package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.single

fun main(): Unit = solveDay(
    9,
//    input = aok.InputProvider.raw("1102,34915192,34915192,7,4,7,99,0")
)

@AoKSolution
object Day09 : PuzDSL({
    val parse = parser { input.split(",").map(String::toLong) }
    part1(parse) { prog ->
        IntcodeCpu(prog).process(1).single()
    }
    part2(parse) { prog ->
        IntcodeCpu(prog).process(2).single()
    }
}) {

    class IntcodeCpu(program: List<Long>) {
        private val input = Channel<Long>(Channel.BUFFERED)
        private lateinit var output: SendChannel<Long>
        private var memory = program.toLongArray()
        private var instr = 0
        private var rel = 0

        fun process(vararg inputs: Long) = channelFlow {
            output = channel
            inputs.forEach { input.send(it) }
            while (instr in memory.indices) execute()
        }

        operator fun get(addr: Int) = if (addr > memory.lastIndex) 0L else memory[addr]
        private fun param(addr: Int, mode: ParameterMode) = get(addr).let {
            when (mode.value) {
                0 -> get(it.toInt())
                1 -> it
                2 -> get((it + rel).toInt())
                else -> error("invalid parameter mode '$mode'")
            }
        }
        private fun addr(addr: Int, mode: ParameterMode) = get(addr).toInt().let {
            when(mode.value) {
                0, 1 -> it
                2 -> rel + it
                else -> error("invalid address mode '$mode'")
            }
        }.toInt()

        operator fun set(addr: Int, value: Long) {
            if (addr > memory.lastIndex) memory = memory.copyOf(addr + 32)
            memory[addr] = value
        }

        private suspend fun execute() {
            val operation = Operation(get(instr++).toInt())
            val (opcode, a, b, c) = operation
            try {
                when (opcode.value) {
                    99 -> halt()
                    1 -> add(param(instr++, a), param(instr++, b), addr(instr++, c))
                    2 -> multiply(param(instr++, a), param(instr++, b), addr(instr++, c))
                    3 -> input(addr(instr++, a))
                    4 -> output(param(instr++, a))
                    5 -> jumpIfTrue(param(instr++, a), param(instr++, b))
                    6 -> jumpIfFalse(param(instr++, a), param(instr++, b))
                    7 -> lessThan(param(instr++, a), param(instr++, b), addr(instr++, c))
                    8 -> equal(param(instr++, a), param(instr++, b), addr(instr++, c))
                    9 -> rel += param(instr++, a).toInt()
                    else -> error("unsupported opcode '$opcode'")
                }
            } catch (e: Exception) {
                error("failed processing ${operation.raw} ($opcode,$a,$b,$c) -> $e")
            }
        }

        private fun halt() {
            instr = -1
        }

        private fun add(a: Long, b: Long, c: Int) = set(c, a + b)
        private fun multiply(a: Long, b: Long, c: Int) = set(c, a * b)
        private suspend fun input(a: Int) = set(a, input.receive())
        private suspend fun output(a: Long) = output.send(a)
        private fun jumpIfTrue(a: Long, b: Long) {
            if (a != 0L) instr = b.toInt()
        }

        private fun jumpIfFalse(a: Long, b: Long) {
            if (a == 0L) instr = b.toInt()
        }

        private fun lessThan(a: Long, b: Long, c: Int) = set(c, if (a < b) 1 else 0)
        private fun equal(a: Long, b: Long, c: Int) = set(c, if (a == b) 1 else 0)

        @JvmInline
        private value class Operation(val raw: Int) {
            operator fun component1() = OpCode(raw % 100)
            operator fun component2() = ParameterMode(raw / 100 % 10)
            operator fun component3() = ParameterMode(raw / 1000 % 10)
            operator fun component4() = ParameterMode(raw / 10000 % 10)
        }

        @JvmInline
        private value class OpCode(val value:Int)
        @JvmInline
        private value class ParameterMode(val value:Int)
    }
}

