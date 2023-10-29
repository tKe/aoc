package year2019

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

fun main(): Unit = solveDay(
    9,
//    input = aok.InputProvider.raw("1102,34915192,34915192,7,4,7,99,0")
)

@AoKSolution
object Day09 : PuzDSL({
    part1(IntcodeProgram) { prog ->
        prog.process(1).single()
    }
    part2(IntcodeProgram) { prog ->
        prog.process(2).single()
    }
}) {

    @JvmInline
    value class IntcodeProgram(val program: List<Long>) {

        fun modify(block: (LongArray) -> Unit) = IntcodeProgram(program.toLongArray().also(block).toList())

        suspend fun execute(inputs: ReceiveChannel<Long>, outputs: SendChannel<Long>) {
            IntcodeCpu(program, inputs, outputs).run()
            outputs.close()
        }

        context(CoroutineScope)
        fun launch(
            inputs: Channel<Long> = Channel(Channel.BUFFERED),
            outputs: Channel<Long> = Channel(Channel.RENDEZVOUS)
        ) = Triple(inputs as SendChannel<Long>, outputs as ReceiveChannel<Long>, launch {
            execute(inputs, outputs)
        })

        @OptIn(ExperimentalCoroutinesApi::class)
        fun process(vararg inputs: Long) = channelFlow {
            execute(produce { for (input in inputs) send(input) }, channel)
        }

        companion object : (PuzzleInput) -> IntcodeProgram {
            override fun invoke(puzzleInput: PuzzleInput) =
                IntcodeProgram(puzzleInput.input.split(",").map(String::toLong))
        }
    }

    class IntcodeCpu internal constructor(
        program: List<Long>,
        private val input: ReceiveChannel<Long>,
        private val output: SendChannel<Long>
    ) {
        private var memory = program.toLongArray()
        private var instr = 0
        private var rel = 0

        suspend fun run() {
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
            when (mode.value) {
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
        private value class OpCode(val value: Int)

        @JvmInline
        private value class ParameterMode(val value: Int)
    }
}

