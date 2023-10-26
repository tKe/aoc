package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import utils.permute
import utils.splitIntsNotNull
import year2019.Day05.IntcodeCpu

fun main(): Unit = solveDay(7)

@AoKSolution
object Day07 : PuzDSL({
    val parse = parser { input.splitIntsNotNull(",") }
    operator fun List<Int>.invoke(phaseSetting: Int) = IntcodeCpu(this).apply { trySend(phaseSetting).getOrThrow() }
    suspend infix fun IntcodeCpu.sendTo(other: IntcodeCpu) = receive().collect(other::send)

    fun CoroutineScope.chain(
        program: List<Int>,
        phaseSettings: Iterable<Int>
    ): Pair<IntcodeCpu, IntcodeCpu> {
        val amps = phaseSettings.map {
            IntcodeCpu(program).also { amp ->
                amp.trySend(it)
                amp.launchIn(this)
            }
        }
        amps.zipWithNext { a, b -> launch { a sendTo b } }
        val chain = amps.first() to amps.last()
        return chain
    }

    part1(parse) { amp ->
        suspend fun ampChain(phaseSettings: Iterable<Int>) = coroutineScope {
            val (start, end) = chain(amp, phaseSettings)
            start.send(0)
            end.receive().last()
        }

        (0..4).permute().maxOf { ampChain(it) }
    }

    part2(parse) { amp ->
        suspend fun ampChain(phaseSettings: Iterable<Int>) = coroutineScope {
            val (start, end) = chain(amp, phaseSettings)
            start.send(0)
            end.receive().onEach(start::send).last()
        }

        (5..9).permute().maxOf { ampChain(it) }
    }
})
