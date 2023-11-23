package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import year2019.Day09.IntcodeCpu
import year2019.Day09.IntcodeProgram

fun main() = solveDay(
    23,
)

@AoKSolution
object Day23 : PuzDSL({
    fun List<NIC>.simulate() = sequence {
        do {
            forEach { nic ->
                nic.process()?.let { (addr, packet) ->
                    if (addr == 255) yield(packet)
                    else get(addr).send(packet)
                }
            }
        } while (!all { it.isIdle })
    }

    part1(IntcodeProgram) { nicCode ->
        List(50) { NIC(it, nicCode) }.simulate().map { it.y }.first()
    }

    fun List<NIC>.simulateWithNat() = sequence {
        while (true) {
            simulate().last().let {
                yield(it)
                get(0).send(it)
            }
        }
    }

    part2(IntcodeProgram) { nicCode ->
        List(50) { NIC(it, nicCode) }.simulateWithNat().map { it.y }
            .zipWithNext { a, b -> a.takeIf(b::equals) }.firstNotNullOf { it }
    }
}) {
    data class Packet(val x: Long, val y: Long)

    class NIC(private val cpu: IntcodeCpu, private val queue: ArrayDeque<Packet> = ArrayDeque()) {
        constructor(id: Int, program: IntcodeProgram) : this(program.load().also { it.send(id) })

        val isIdle get() = cpu.advance() is IntcodeCpu.Input && queue.isEmpty()

        fun process() = when (cpu.advance()) {
            IntcodeCpu.Halt -> error("nic died")
            is IntcodeCpu.Output -> cpu.receive().toInt() to Packet(cpu.receive(), cpu.receive())
            is IntcodeCpu.Input -> null.also {
                queue.removeFirstOrNull()
                    ?.let { (x, y) -> cpu.send(x, y) }
                    ?: cpu.send(-1)
            }
        }

        fun send(packet: Packet) = queue.addLast(packet)
    }
}


