package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import utils.splitIntsNotNull

fun main(): Unit = solveDay(5)

@AoKSolution
object Day05 : PuzDSL({
    part1 {
        coroutineScope {
            val cpu = IntcodeCpu(input.splitIntsNotNull(","))
            cpu.launchIn(this)
            cpu.send(1)
            cpu.receive().last()
        }
    }
    part2 {
        coroutineScope {
            val cpu = IntcodeCpu(input.splitIntsNotNull(","))
            cpu.launchIn(this)
            cpu.send(5)
            cpu.receive().single()
        }
    }
}) {
    class IntcodeCpu(program: List<Int>) {
        private val input = Channel<Int>(Channel.BUFFERED)
        private val output = Channel<Int>()
        private val memory = program.toIntArray()
        private var instr = 0

        suspend fun send(value: Int) = input.send(value)
        fun trySend(value: Int) = input.trySend(value)
        fun receive() = output.receiveAsFlow()

        operator fun get(addr: Int) = memory[addr]
        operator fun get(addr: Int, mode: Int) = get(addr).let {
            when(mode) {
                0 -> get(it)
                1 -> it
                else -> error("invalid parameter mode '$mode'")
            }
        }
        operator fun set(addr: Int, value: Int) = memory.set(addr, value)
        
        fun launchIn(scope: CoroutineScope) = scope.launch {
            while(instr in memory.indices) execute()
            output.close()
        }

        private suspend fun execute() {
            val (opcode, a, b) = Operation(get(instr++))
            when(opcode) {
                99 -> halt()
                1 -> add(get(instr++, a), get(instr++, b), get(instr++))
                2 -> multiply(get(instr++, a), get(instr++, b), get(instr++))
                3 -> input(get(instr++))
                4 -> output(get(instr++))
                5 -> jumpIfTrue(get(instr++, a), get(instr++, b))
                6 -> jumpIfFalse(get(instr++, a), get(instr++, b))
                7 -> lessThan(get(instr++, a), get(instr++, b), get(instr++))
                8 -> equal(get(instr++, a), get(instr++, b), get(instr++))
                else -> error("unsupported opcode '$opcode'")
            }
        }

        private fun halt() {
            instr = -1
        }
        private fun add(a: Int, b: Int, c: Int) = set(c, a + b)
        private fun multiply(a: Int, b: Int, c: Int) = set(c, a * b)
        private suspend fun input(a: Int) = set(a, input.receive())
        private suspend fun output(a: Int) = output.send(get(a))
        private fun jumpIfTrue(a: Int, b: Int) {
            if(a != 0) instr = b
        }

        private fun jumpIfFalse(a: Int, b: Int) {
            if(a == 0) instr = b
        }
        private fun lessThan(a: Int, b: Int, c: Int) = set(c, if(a < b) 1 else 0)
        private fun equal(a: Int, b: Int, c: Int) = set(c, if(a == b) 1 else 0)

        @JvmInline
        private value class Operation(val a: Int) {
            operator fun component1(): Int = a % 100
            operator fun component2(): Int = a / 100 % 10
            operator fun component3(): Int = a / 1000 % 10
            operator fun component4(): Int = a / 10000 % 10
        }
    }
}

