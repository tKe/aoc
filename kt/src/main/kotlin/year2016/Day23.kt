package year2016

import aok.PuzDSL
import aoksp.AoKSolution
import arrow.core.unzip
import year2016.Day23.inv
import year2016.Day23.optimize

@AoKSolution
object Day23 : PuzDSL({
    val parseInstructions = parser {
        lines.unzip { instr ->
            val (cmd, args) = instr.split(" ", limit = 2)
            Instruction.of(cmd) to args.split(" ").let { Argument.of(it.first()) to Argument.of(it.last()) }
        }
    }

    fun Pair<List<Instruction>, List<Pair<Argument, Argument>>>.execute(
        a: Int = 0,
        b: Int = 0,
        c: Int = 0,
        d: Int = 0,
        optimize: Boolean = false
    ) =
        let { (program, arguments) ->
            object : State {
                var instruction = 0
                val instructions = program.toTypedArray()
                val registers = intArrayOf(a, b, c, d)
                override fun toggle(ofs: Int) {
                    val idx = instruction + ofs
                    if (idx in instructions.indices) instructions[idx] = instructions[idx].inv()
                    if (optimize) instructions.optimize(arguments)
                }

                override fun set(reg: Int, value: Int) {
                    registers[reg] = value
                }

                override fun get(reg: Int) = registers[reg]
                fun execute(): Int {
                    while (instruction in instructions.indices) {
                        instruction += with(instructions[instruction]) { invoke(arguments[instruction]) }
                    }
                    return registers[0]
                }
            }.execute()
        }

    part1 { parseInstructions().execute(a = 7) }
    part2 { parseInstructions().execute(a = 12, optimize = true) }
}) {
    interface State {
        fun toggle(ofs: Int)
        operator fun set(reg: Int, value: Int)
        operator fun get(reg: Int): Int
    }

    sealed interface Argument {
        context(State)
        val value: Int

        data class Constant(val const: Int) : Argument {
            override fun toString() = "$const"
            context(State) override val value: Int get() = const
        }

        data class Register(private val id: Int) : Argument {
            override fun toString() = "${'a' + id}"
            context(State) override var value
                get() = get(id)
                set(v) = set(id, v)
        }

        companion object {
            fun of(arg: String) = arg.toIntOrNull()?.let(::Constant) ?: Register(arg[0] - 'a')
        }
    }

    fun interface Operation {
        operator fun State.invoke(args: Pair<Argument, Argument>): Int
    }

    inline fun operation(jmp: Int = 1, crossinline block: State.(Pair<Argument, Argument>) -> Unit) =
        Operation { block(it); jmp }

    inline fun <reified A : Argument> op(crossinline block: State.(A) -> Unit) =
        operation { (a) -> if (a is A) block(a) }

    inline fun <reified A : Argument, reified B : Argument> op(crossinline block: State.(A, B) -> Unit) =
        operation { (a, b) -> if (a is A && b is B) block(a, b) }

    fun Instruction.inv(): Instruction = when (this) {
        Copy -> JumpNonZero
        JumpNonZero -> Copy
        Increment -> Decrement
        Decrement, ToggleInstruction -> Increment
        is Optimization -> realInstruction.inv()
    }

    object Copy : Instruction(op<Argument, Argument.Register> { from, to -> to.value = from.value })
    object JumpNonZero : Instruction({ (test, ofs) -> if (test.value != 0) ofs.value else 1 })
    object Increment : Instruction(op<Argument.Register> { it.value++ })
    object Decrement : Instruction(op<Argument.Register> { it.value-- })
    object ToggleInstruction : Instruction(op<Argument> { toggle(it.value) })

    class Optimization(val realInstruction: Instruction, operation: Operation) : Instruction(operation)

    fun Array<Instruction>.optimize(arguments: List<Pair<Argument, Argument>>) {
        findSubsequences(Copy, Increment, Decrement, JumpNonZero, Decrement, JumpNonZero)
            .forEach { range ->
                arguments.slice(range).let { args ->
                    val (n, r1) = args[0]
                    val (r2) = args[1]
                    val (r1a) = args[2]
                    val (r1b, m2) = args[3]
                    val (r3) = args[4]
                    val (r3a, m5) = args[5]

                    if (r1 is Argument.Register && r1 == r1a && r1 == r1b
                        && r2 is Argument.Register
                        && r3 is Argument.Register && r3 == r3a
                        && setOf(n, r1, r2, r3).size == 4
                        && (m2 as? Argument.Constant)?.const == -2 && (m5 as? Argument.Constant)?.const == -5
                    ) set(range.first, Optimization(Copy, operation(6) {
                        r2.value += r3.value * n.value
                        r3.value = 0
                        r1.value = 0
                    }))
                }
            }
    }

    private fun Array<Instruction>.findSubsequences(start: Instruction, vararg subsequence: Instruction) = sequence {
        for ((index, instruction) in withIndex()) if (instruction == start) {
            var i = index
            if (subsequence.all { get(++i) == it }) {
                yield(index..index + subsequence.size)
            }
        }
    }

    sealed class Instruction(operation: Operation) : Operation by operation {
        companion object {
            fun of(cmd: String) = when (cmd) {
                "cpy" -> Copy
                "inc" -> Increment
                "dec" -> Decrement
                "jnz" -> JumpNonZero
                "tgl" -> ToggleInstruction
                else -> error("Unknown instruction '$cmd'")
            }
        }
    }
}

fun main() = solveDay(
    23,
//    input = InputProvider.raw(
//        """
//        cpy 2 a
//        tgl a
//        tgl a
//        tgl a
//        cpy 1 a
//        dec a
//        dec a
//    """.trimIndent()
//    )
)
