package year2019

import aok.Parser
import aok.PuzDSL
import aoksp.AoKSolution
import utils.replace
import year2019.Day09.IntcodeCpu
import year2019.Day09.IntcodeProgram
import java.io.Closeable

fun main() = solveDay(
    21,
)

@AoKSolution
object Day21 : PuzDSL({
    part1(SpringDroid) { springDroid ->
        springDroid.execute(
            """
                NOT A J # jump if we're at an edge
                NOT C T # T: if there's a no land in three
                AND D T # T: and there is land in four (where we'd land)
                OR T J  # J: T or !A
                WALK
            """.trimIndent()
        )
    }

    part2(SpringDroid) { springDroid ->
        // As we can jump 4 spaces, we're only interested in how early we can jump gaps as they approach through C->B->A
        // C: only jump C if we can safely land, jump and land again (4+4=H is ground)
        // otherwise only ever jump if we can safely land
        springDroid.execute(
            """
                # (!C && H) -> J
                NOT C J
                AND H J
                
                # (J || !B) -> J
                NOT B T
                OR T J
                
                # (J || !A) -> J
                NOT A T
                OR T J
                
                # (J && D) -> J
                AND D J
                RUN
            """.trimIndent()
        )
    }
}) {
    @JvmInline
    value class AsciiCpu(private val cpu: IntcodeCpu) : Closeable {
        val isRunning get() = cpu.advance() !is IntcodeCpu.Halt
        override fun close() = require(cpu.advance() == IntcodeCpu.Halt)
        fun read(): Output {
            val stdout = StringBuilder()
            for (output in cpu.receiveSequence()) {
                if (output > Short.MAX_VALUE) return Result(output)
                else stdout.append(output.toInt().toChar())
            }
            return Message(stdout.toString())
        }

        fun write(input: String, preprocess: Boolean = true) {
            for (line in input.lines()) {
                val statement = when {
                    preprocess -> line.substringBefore('#').trim().takeUnless { it.isBlank() }
                    else -> line
                } ?: continue

                statement.forEach { cpu.send(it.code.toLong()) }
                cpu.send(10L)
            }
        }

        sealed interface Output
        data class Message(val value: String) : Output {
            override fun toString() = value
        }

        data class Result(val value: Long) : Output {
            override fun toString() = value.toString()
        }
    }

    @JvmInline
    value class SpringDroid(private val program: IntcodeProgram) {
        companion object : Parser<SpringDroid> by IntcodeProgram.map(::SpringDroid)

        fun execute(script: String) = AsciiCpu(program.load()).use {
            require(it.read() == AsciiCpu.Message("Input instructions:\n"))
            it.write(script)
            when (val output = it.read()) {
                is AsciiCpu.Result -> output
                is AsciiCpu.Message -> output.emojify()
            }
        }

        private fun AsciiCpu.Message.emojify() = copy(value = value.lineSequence().joinToString("\n") {
            when {
                it.isEmpty() || it[0] !in ".#@" -> it
                else -> it.replace("." to "⚫", "#" to "🟫", "@" to "🤖")
            }
        })
    }
}
