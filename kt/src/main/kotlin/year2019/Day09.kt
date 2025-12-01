package year2019

import aok.Parser
import aok.PuzDSL
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
import year2019.Day09.IntcodeCpu.Input
import year2019.Day09.IntcodeCpu.Output

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
    private val instrParser = Parser { input.split(",").map(String::toLong) }
    @JvmInline
    value class IntcodeProgram(val program: List<Long>) {
        fun load() = IntcodeCpu(program)

        fun modify(block: (LongArray) -> Unit) = IntcodeProgram(program.toLongArray().also(block).toList())

        private suspend fun execute(inputs: ReceiveChannel<Long>, outputs: SendChannel<Long>) {
            load().run(inputs, outputs)
            outputs.close()
        }

        context(scope: CoroutineScope)
        fun launch(
            inputs: Channel<Long> = Channel(Channel.BUFFERED),
            outputs: Channel<Long> = Channel(Channel.RENDEZVOUS)
        ) = Triple(inputs as SendChannel<Long>, outputs as ReceiveChannel<Long>, scope.launch {
            execute(inputs, outputs)
        })


        private suspend fun IntcodeCpu.run(input: ReceiveChannel<Long>, output: SendChannel<Long>) {
            while (true) {
                when (val interrupt = advance()) {
                    IntcodeCpu.Halt -> break
                    is Input -> interrupt.set(input.receive())
                    is Output -> output.send(interrupt.read())
                }
            }
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        fun process(vararg inputs: Long) = channelFlow {
            execute(produce { for (input in inputs) send(input) }, channel)
        }

        companion object : Parser<IntcodeProgram> by instrParser.map(::IntcodeProgram)
    }

    class IntcodeCpu private constructor(
        private var memory: LongArray,
        private var instr: Int = 0,
        private var rel: Int = 0,
    ) {
        companion object : Parser<IntcodeCpu> by instrParser.map(::IntcodeCpu)
        internal constructor(program: List<Long>) : this(program.toLongArray())

        fun trySend(value: Long) = (advance() as? Input)?.set(value) != null
        fun send(value: Long) = with(advance()) {
            require(this is Input) { "expected Input but was $this" }
            set(value)
        }

        fun send(vararg inputs: Int) = inputs.forEach { send(it.toLong()) }
        fun send(vararg inputs: Long) = inputs.forEach { send(it) }

        fun tryReceive() = (advance() as? Output)?.read()
        fun receive() = with(advance()) {
            require(this is Output)
            read()
        }

        fun receiveSequence() = generateSequence { (advance() as? Output)?.read() }

        tailrec fun advance(): Interrupt {
            val (opcode, a, b, c) = Operation(get(instr).toInt())
            return when (opcode.value) {
                99 -> Halt
                3 -> In(a)
                4 -> Out(a)
                else -> {
                    instr++
                    when (opcode.value) {
                        1 -> add(param(instr++, a), param(instr++, b), addr(instr++, c))
                        2 -> multiply(param(instr++, a), param(instr++, b), addr(instr++, c))
                        5 -> jumpIfTrue(param(instr++, a), param(instr++, b))
                        6 -> jumpIfFalse(param(instr++, a), param(instr++, b))
                        7 -> lessThan(param(instr++, a), param(instr++, b), addr(instr++, c))
                        8 -> equal(param(instr++, a), param(instr++, b), addr(instr++, c))
                        9 -> rel += param(instr++, a).toInt()
                        else -> error("unsupported opcode '$opcode'")
                    }
                    advance()
                }
            }
        }

        operator fun get(addr: Int) = if (addr > memory.lastIndex) 0L else memory[addr]

        operator fun set(addr: Int, value: Long) {
            if (addr > memory.lastIndex) memory = memory.copyOf(addr + 32)
            memory[addr] = value
        }

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

        private fun add(a: Long, b: Long, c: Int) = set(c, a + b)
        private fun multiply(a: Long, b: Long, c: Int) = set(c, a * b)
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

        private abstract inner class IO {
            protected val a = instr
                get() {
                    require(instr == field)
                    instr++
                    return instr++
                }

            override fun toString() = this::class.simpleName ?: "IO"
        }

        private inner class In(private val mode: ParameterMode) : IO(), Input {
            override fun set(value: Long) = set(addr(a, mode), value)
        }

        private inner class Out(private val mode: ParameterMode) : IO(), Output {
            override fun read() = param(a, mode)
        }

        sealed interface Interrupt
        fun interface Input : Interrupt {
            fun set(value: Long)
            fun set(value: Int) = set(value.toLong())
        }

        fun interface Output : Interrupt {
            fun read(): Long
        }

        data object Halt : Interrupt

        fun snapshot(): Snapshot = State(memory.copyOf(), instr, rel)
        fun restore(snapshot: Snapshot) = when (snapshot) {
            is State -> {
                memory = snapshot.memory.copyOf()
                instr = snapshot.instr
                rel = snapshot.rel
            }
        }

        private class State(val memory: LongArray, val instr: Int, val rel: Int) : Snapshot {
            override fun fork() = IntcodeCpu(memory.copyOf(), instr, rel)
        }

        sealed interface Snapshot {
            fun fork(): IntcodeCpu
        }
    }
}
