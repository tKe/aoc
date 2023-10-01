@file:OptIn(ExperimentalCoroutinesApi::class)

package year2017

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import year2017.Day18.Instr
import year2017.Day18.named

@AoKSolution
object Day18 : PuzDSL({

    fun String.toRegister() = this[0] - 'a'
    fun String.asValueProvider(): Executor.() -> Long =
        toLongOrNull()?.let { { it } } ?: { this[toRegister()] }

    fun modify(reg: Int, value: Executor.() -> Long, f: (reg: Long, value: Long) -> Long): Instr {
        return Instr {
            set(reg, f(get(reg), value()))
            jmp(1)
        }
    }

    fun PuzzleInput.parseInstructions(): List<Instr> = lines
        .map { it.split(" ") }
        .map { op: List<String> ->
            val instr = op[0]
            val reg = op[1].toRegister()
            val value = op.getOrNull(2)?.asValueProvider() ?: { error("no value for instr") }
            when (instr) {
                "set" -> modify(reg, value) { _, it -> it }
                "add" -> modify(reg, value, Long::plus)
                "mul" -> modify(reg, value, Long::times)
                "mod" -> modify(reg, value, Long::rem)

                "jgz" -> {
                    val chk = op[1].asValueProvider()
                    Instr {
                        if (chk() > 0L) jmp(value().toInt()) else jmp(1)
                    }
                }

                "snd" -> Instr {
                    snd(reg)
                    jmp(1)
                }

                "rcv" -> Instr {
                    rcv(reg)
                    jmp(1)
                }

                else -> TODO()
            } named op.joinToString(" ").let { "`$it`" }
        }

    part1 {
        val executor = object : BaseExecutor() {
            var snd = 0L
            override suspend fun snd(reg: Int) {
                snd = get(reg)
            }

            override suspend fun rcv(reg: Int) {
                if (get(reg) != 0L) {
                    halt()
                }
            }
        }
        executor(parseInstructions())
        executor.snd
    }

    part2 {
        class Prog(private val id: Int) : BaseExecutor() {
            private var waiting = false
            private val output: Channel<Long> = Channel(capacity = Channel.UNLIMITED)
            var source: Prog? = null
            var sent = 0

            init {
                set("p".toRegister(), id.toLong())
            }

            override suspend fun snd(reg: Int) {
                output.send(get(reg))
                sent++
            }

            override suspend fun rcv(reg: Int) {
                val source = source ?: error("output not defined")
                val rcv = source.output.tryReceive()
                when {
                    rcv.isSuccess -> set(reg, rcv.getOrThrow())
                    source.waiting && output.isEmpty -> halt()
                    rcv.isFailure -> {
                        waiting = true
                        source.output.receiveCatching().let {
                            waiting = false
                            if(it.isClosed) halt()
                            else set(reg,it.getOrThrow())
                        }
                    }
                }
            }

            override fun halt() {
                output.close()
                super.halt()
            }

            override fun toString() = "Prog($id)"
        }

        val prog0 = Prog(0)
        val prog1 = Prog(1)
        prog0.source = prog1
        prog1.source = prog0

        val instrs = parseInstructions()
        coroutineScope {
            launch { prog0(instrs) }
            launch { prog1(instrs) }
        }

        prog1.sent
    }

}) {
    interface Executor {
        operator fun get(reg: Int): Long
        operator fun set(reg: Int, value: Long)
        suspend fun snd(reg: Int)
        suspend fun rcv(reg: Int)
        fun jmp(by: Int)
    }

    abstract class BaseExecutor : Executor {
        private val regs = LongArray(26)
        private var instr = 0
        override fun get(reg: Int) = regs[reg]
        override fun set(reg: Int, value: Long) = regs.set(reg, value)
        override fun jmp(by: Int) {
            instr += by
        }

        open fun halt() {
            instr = Int.MAX_VALUE
        }

        suspend operator fun invoke(instrs: List<Instr>) {
            while (instr in instrs.indices) {
                with(instrs[instr]) { execute() }
            }
        }
    }

    fun interface Instr {
        suspend fun Executor.execute()
    }

    infix fun Instr.named(name: String) = object : Instr by this {
        override fun toString() = name
    }
}

fun main(): Unit = solveDay(
    18,
//    warmup = Warmup.eachFor(5.seconds), runs = 3,
)
