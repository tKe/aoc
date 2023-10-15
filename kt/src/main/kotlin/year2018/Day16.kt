package year2018

import aok.PuzDSL
import aok.sealedObjects
import aoksp.AoKSolution
import year2018.Day16.Calc
import year2018.Day16.Check
import year2018.Day16.reg
import kotlin.time.Duration.Companion.seconds

fun main(): Unit = solveDay(
    16,
    warmup = aok.Warmup.eachFor(3.seconds), runs = 250,
)

@AoKSolution
object Day16 : PuzDSL({

    fun String.intSequence() = Regex("[^0-9]+").splitToSequence(this).mapNotNull(String::toIntOrNull)
    data class Sample(val before: List<Int>, val instr: List<Int>, val after: List<Int>) {
        infix fun behavesLike(op: Op) = before.toIntArray().apply {
            op.invoke(instr[1], instr[2], instr[3])
        }.contentEquals(after.toIntArray())
    }


    fun Sequence<Sample>.determineOpCodes() = Array<Op>(16) { noop }.also { opCodes ->
        val candidates = groupingBy { it.instr[0] }.foldTo(
            mutableMapOf(),
            { _, sample -> Op.all.filterTo(mutableSetOf(), sample::behavesLike) }
        ) { _, ops: MutableSet<Op>, sample ->
            ops.removeIf { !(sample behavesLike it) }
            ops
        }

        while (candidates.isNotEmpty()) {
            val (op, opCode) = candidates.firstNotNullOf { (opCode, ops) -> ops.singleOrNull()?.to(opCode) }
            opCodes[opCode] = op
            candidates -= opCode
            candidates.forEach { (_, ops) -> ops -= op }
        }
    }

    val parse = parser {
        val (samples, instructions) = input.split("\n\n\n\n")

        val opSamples = samples.intSequence().chunked(4).chunked(3) { (before, instr, after) ->
            Sample(before, instr, after)
        }

        val program = instructions.intSequence().chunked(4)
        opSamples to program
    }

    part1(parse) { (samples) ->
        samples.count { Op.all.count(it::behavesLike) >= 3 }
    }

    part2(parse) { (samples, program) ->
        val ops = samples.determineOpCodes()
        with(IntArray(4)) {
            program.forEach { (op, a, b, c) -> ops[op](a, b, c) }
            0.reg
        }
    }

}) {

    context(IntArray)
    var Int.reg
        get() = get(this)
        set(value) = set(this@reg, value)

    sealed interface Op {
        context(IntArray)
        operator fun invoke(a: Int, b: Int, c: Int)

        companion object {
            val all = Op::class.sealedObjects.toSet() - noop
            fun custom(name: String, impl: IntArray.(a: Int, b: Int, c: Int) -> Unit): Op = object : CustomOp(name) {
                context(IntArray) override fun invoke(a: Int, b: Int, c: Int) {
                    this@IntArray.impl(a, b, c)
                }

                override fun toString() = name
            }
        }

        private abstract class CustomOp(private val name: String) : Op
    }

    private fun interface Calc : Op {
        context(IntArray) fun calculate(a: Int, b: Int): Int
        context(IntArray) override fun invoke(a: Int, b: Int, c: Int) {
            c.reg = calculate(a, b)
        }
    }

    private fun interface Check : Calc {
        context(IntArray)  fun check(a: Int, b: Int): Boolean
        context(IntArray) override fun calculate(a: Int, b: Int) = if (check(a, b)) 1 else 0
    }

    data object noop : Op {
        context(IntArray) override fun invoke(a: Int, b: Int, c: Int) {}
    }

    data object addr : Op by (Calc { a, b -> a.reg + b.reg })
    data object addi : Op by (Calc { a, b -> a.reg + b })
    data object mulr : Op by (Calc { a, b -> a.reg * b.reg })
    data object muli : Op by (Calc { a, b -> a.reg * b })
    data object banr : Op by (Calc { a, b -> a.reg and b.reg })
    data object bani : Op by (Calc { a, b -> a.reg and b })
    data object borr : Op by (Calc { a, b -> a.reg or b.reg })
    data object bori : Op by (Calc { a, b -> a.reg or b })
    data object setr : Op by (Calc { a, b -> a.reg })
    data object seti : Op by (Calc { a, b -> a })
    data object gtir : Op by (Check { a, b -> a > b.reg })
    data object gtri : Op by (Check { a, b -> a.reg > b })
    data object gtrr : Op by (Check { a, b -> a.reg > b.reg })
    data object eqir : Op by (Check { a, b -> a == b.reg })
    data object eqri : Op by (Check { a, b -> a.reg == b })
    data object eqrr : Op by (Check { a, b -> a.reg == b.reg })

}

