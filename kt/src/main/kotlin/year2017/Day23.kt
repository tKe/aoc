package year2017

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

@AoKSolution
object Day23 : PuzDSL({
    fun String.toRegister() = this[0] - 'a'
    fun PuzzleInput.parseInstructions() = lines
        .map { it.split(" ") }
        .map { op: List<String> ->
            val instr = op[0]
            val reg = op[1].toRegister()
            when (instr) {
                "set" -> Instr { set(reg, Value.of(op[2])) }
                "sub" -> Instr { sub(reg, Value.of(op[2])) }
                "mul" -> Instr { mul(reg, Value.of(op[2])) }
                "jnz" -> Instr { jnz(Value.of(op[1]), Value.of(op[2])) }
                else -> TODO()
            }
        }

    part1 {
        var count = 0L
        object : BaseExecutor() {
            override fun mul(reg: Int, value: Value) = super.mul(reg, value).also { count++ }
        }.execute(parseInstructions())
        count
    }

    part2 {
        val (_, b, c) = BaseExecutor('a' to 1).execute(parseInstructions().take(8))
        fun Long.isPrime() = (2..<this / 2).any { this % it == 0L }
        (b..c step 17).count(Long::isPrime)
    }

}) {
    sealed interface Value {
        context(_: Executor)
        val value: Long

        @JvmInline
        value class Const(private val const: Long) : Value {
            context(_: Executor) override val value: Long
                get() = const
        }
        data class Register(val reg: Int) : Value {
            context(executor: Executor)
            override val value
                get() = executor.get(reg)
        }

        companion object {
            fun of(repr: String) = when (val value = repr.toLongOrNull()) {
                null -> Register(repr[0] - 'a')
                else -> Const(value)
            }
        }
    }

    interface Executor {
        fun jmp(by: Int)
        operator fun get(reg: Int): Long
        operator fun set(reg: Int, value: Long)
        operator fun get(reg: Char) = get(reg - 'a')
        operator fun set(reg: Char, value: Long) = set(reg - 'a', value)

        fun set(reg: Int, value: Value) = set(reg, value.value).also { jmp(1) }

        fun mul(reg: Int, value: Value) = set(reg, get(reg) * value.value).also { jmp(1) }

        fun sub(reg: Int, value: Value) = set(reg, get(reg) - value.value).also { jmp(1) }

        fun jnz(chk: Value, by: Value) = jmp(if (chk.value != 0L) by.value.toInt() else 1)
    }

    open class BaseExecutor(vararg initials: Pair<Char, Long>) : Executor {
        private val regs = LongArray(9, initials.associate { (r, v) -> r - 'a' to v }.withDefault { 0 }::getValue)
        private var instr = 0
        override fun get(reg: Int) = regs[reg]
        override fun set(reg: Int, value: Long) = regs.set(reg, value)
        override fun jmp(by: Int) {
            instr += by
        }

        fun execute(instrs: List<Instr>): LongArray {
            while (instr in instrs.indices) {
                with(instrs[instr]) { execute() }
            }
            return regs.copyOf()
        }
    }

    fun interface Instr {
        fun Executor.execute()
    }

}

fun main(): Unit = solveDay(
    23,
//    warmup = Warmup.eachFor(5.seconds), runs = 3,
)
