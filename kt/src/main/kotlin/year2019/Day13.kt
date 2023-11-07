package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.selects.SelectBuilder
import kotlinx.coroutines.selects.selectUnbiased
import year2019.Day09.IntcodeProgram
import year2019.Day13.BALL
import year2019.Day13.BLOCK
import year2019.Day13.PADDLE

fun main() = solveDay(13)

@AoKSolution
object Day13 : PuzDSL({
    fun <T> Flow<T>.chunked(n: Int) = flow {
        val chunk = ArrayDeque<T>(n)
        collect {
            chunk += it
            if (chunk.size == n) emit(chunk.toList().also { chunk.clear() })
        }
    }

    suspend fun IntcodeProgram.countBlocks() = buildSet {
        coroutineScope {
            process().chunked(3).collect { (x, y, t) ->
                val k = x.shl(16) + y
                if (t == BLOCK) add(k) else remove(k)
            }
        }
    }.size

    part1(IntcodeProgram) { prog ->
        generateSequence(Day09.IntcodeCpu(prog.program)::advance)
        prog.countBlocks()
    }

    suspend fun whileSelectUnbiased(select: SelectBuilder<Boolean>.() -> Unit) {
        @Suppress("ControlFlowWithEmptyBody")
        while(selectUnbiased(select)) {}
    }

    suspend fun IntcodeProgram.winGame() = coroutineScope {
        val (input, output) = launch(inputs = Channel(Channel.RENDEZVOUS))
        var score = 0L
        var ball = -1L
        var paddle = -1L

        whileSelectUnbiased {
            input.onSend(ball.compareTo(paddle).toLong()) { true }
            output.onReceiveCatching { rx ->
                rx.onSuccess { x ->
                    val y = output.receive()
                    val v = output.receive()
                    when {
                        x == -1L && y == 0L -> score = v
                        v == PADDLE -> paddle = x
                        v == BALL -> ball = x
                    }
                }.isSuccess
            }
        }

        score
    }

    part2(IntcodeProgram) { prog ->
        prog.modify { it[0] = 2 }.winGame()
    }

}) {
    const val BLOCK = 2L
    const val PADDLE = 3L
    const val BALL = 4L
}
