package year2022

import InputScope
import PuzDSL
import PuzzleDefinition
import aoksp.AoKSolution
import solveAll

fun main() = queryDay(day = 5).solveAll(runIterations = 1_000)

@AoKSolution
object Day05 : PuzDSL({
    data class Instruction(val move: Int, val from: Int, val to: Int)

    fun InputScope.parse(): Pair<Map<Int, ArrayDeque<Char>>, List<Instruction>> {
        val (rawStacks, rawInstructions) = input.split("\n\n").map(String::lines)
        val stacks = rawStacks.last().mapIndexedNotNull { index, c -> c.digitToIntOrNull()?.to(index) }
        val stackMap = buildMap<_, ArrayDeque<Char>> {
            rawStacks.reversed().drop(1).forEach { line ->
                stacks.forEach { (key, idx) ->
                    val stack = getOrPut(key, ::ArrayDeque)
                    line[idx].takeIf { it in 'A'..'Z' }
                        ?.also { stack.addFirst(it) }
                }
            }
        }

        val instructions = rawInstructions.map {
            it.split(' ').mapNotNull(String::toIntOrNull)
                .let { (move, from, to) -> Instruction(move, from, to) }
        }

        return stackMap to instructions
    }

    fun Map<Int, ArrayDeque<Char>>.topCrates() = buildString {
        for (i in 1..keys.max()) {
            append(getValue(i).firstOrNull() ?: "")
        }
    }

    part1 {
        val (stacks, instructions) = parse()
        instructions.forEach { (move, from, to) ->
            repeat(move) {
                stacks.getValue(to).addFirst(stacks.getValue(from).removeFirst())
            }
        }
        stacks.topCrates()
    }

    part2 {
        val (stacks, instructions) = parse()
        instructions.forEach { (move, from, to) ->
            val crane = ArrayDeque<Char>()
            repeat(move) { crane.addFirst(stacks.getValue(from).removeFirst()) }
            repeat(move) { stacks.getValue(to).addFirst(crane.removeFirst()) }
        }
        stacks.topCrates()
    }
})

@AoKSolution
object Day05Strings : PuzDSL({

    fun List<String>.parseStacks(): List<String> =
        (1..last().length step 4).map { stack ->
            (0 until lastIndex)
                .joinToString("") { "${this[it][stack]}".trim() }
        }

    fun InputScope.rearrange(
        transfer: (from: String, to: String, n: Int) -> Pair<String, String>
    ) =
        input.split("\n\n", limit = 2).map(String::lines)
            .let { (stacks, instructions) ->
                instructions
                    .fold(stacks.parseStacks()) { state, instruction ->
                        val (n, from, to) = instruction.split(' ').mapNotNull(String::toIntOrNull)
                        val (updatedFrom, updatedTo) = transfer(state[from - 1], state[to - 1], n)
                        state.mapIndexed { stack, contents ->
                            when (stack + 1) {
                                from -> updatedFrom
                                to -> updatedTo
                                else -> contents
                            }
                        }
                    }
                    .joinToString("") { "${it.first()}" }
            }

    part1 {
        rearrange { from, to, n ->
            (from.substring(n) to (from.substring(0, n).reversed() + to))
        }
    }
    part2 {
        rearrange { from, to, n ->
            from.substring(n) to (from.substring(0, n) + to)
        }
    }
})
