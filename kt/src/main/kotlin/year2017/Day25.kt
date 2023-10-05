package year2017

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

@AoKSolution
object Day25 : PuzDSL({

    data class Work(val toWrite: Boolean, val moveBy: Int, val nextState: Char)
    data class Procedure(val ifFalse: Work, val ifTrue: Work) {
        operator fun get(bit: Boolean) = if (bit) ifTrue else ifFalse
    }

    data class Blueprint(val startState: Char = 'A', val checksumSteps: Int, val states: Map<Char, Procedure>)

    fun PuzzleInput.parseBlueprint(): Blueprint {
        val startState = lines[0].last { it.isLetter() }
        val checksum = lines[1].split(" ").firstNotNullOf { it.toIntOrNull() }
        val states = lines.asSequence().filter { it.trim().startsWith('-') }
            .map { it.split(" ").last().trimEnd('.') }
            .chunked(3).map { (w, m, s) -> Work(w.toInt() == 1, if (m[0] == 'l') -1 else 1, s[0]) }
            .chunked(2) { (z, o) -> Procedure(z, o) }
            .withIndex().associate { (idx, p) -> 'A' + idx to p }
        return Blueprint(startState, checksum, states)
    }

    part1 {
        val blueprint = parseBlueprint()
        SetTape().apply {
            var state = blueprint.startState
            repeat(blueprint.checksumSteps) {
                blueprint.states[state]!![bit].run {
                    bit = toWrite
                    move(moveBy)
                    state = nextState
                }
            }
        }.checksum()
    }
}) {
    interface Tape {
        var bit: Boolean
        fun move(by: Int)
        fun checksum(): Int
    }

    class SetTape : Tape {
        private val ints = mutableSetOf<Int>()
        private var cursor = 0

        override var bit
            get() = cursor in ints
            set(bit) = if (bit) ints += cursor else ints -= cursor

        override fun move(by: Int) {
            cursor += by
        }

        override fun checksum() = ints.size
    }
}

fun main(): Unit = solveDay(
    25,
)
