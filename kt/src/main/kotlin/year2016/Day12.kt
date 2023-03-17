package year2016

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

@AoKSolution
object Day12 : PuzDSL({
    fun PuzzleInput.parseInstructions() = lines.map { instr ->
        val (cmd, x, y) = "$instr ".split(" ")
        fun reg(arg: String) = arg[0] - 'a'
        fun instr(block: IntArray.() -> Int) = block
        fun op(block: IntArray.() -> Unit) = instr { block(); 1 }

        when (cmd) {
            "cpy" -> {
                val dst = reg(y)
                x.toIntOrNull()?.let { op { this[dst] = it } }
                    ?: reg(x).let { op { this[dst] = this[it] } }
            }

            "inc" -> reg(x).let { op { this[it]++ } }
            "dec" -> reg(x).let { op { this[it]-- } }
            "jnz" -> {
                val j = y.toInt()
                x.toIntOrNull()?.let { if (it != 0) instr { j } else op {} }
                    ?: reg(x).let {
                        instr { if (this[it] != 0) j else 1 }
                    }
            }

            else -> TODO()
        }
    }

    fun List<IntArray.() -> Int>.execute(a: Int = 0, b: Int = 0, c: Int = 0, d: Int = 0) =
        with(intArrayOf(a, b, c, d)) {
            var i = 0
            while (i in this@execute.indices) i += this@execute[i]()
            get(0)
        }

    part1 {
        parseInstructions().execute()
    }

    part2 {
        parseInstructions().execute(c = 1)
    }
})

fun main() = solveDay(
    12,
//    input = InputProvider.raw(
//        """
//        cpy 41 a
//        inc a
//        inc a
//        dec a
//        jnz a 2
//        dec a
//    """.trimIndent()
//    )
)
