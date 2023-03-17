package year2016

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution
import java.util.*

@AoKSolution
object Day21 : PuzDSL({
    fun CharArray.swap(from: Int, to: Int) {
        val c = get(from)
        set(from, get(to))
        set(to, c)
    }

    fun CharArray.swap(from: Char, to: Char) = forEachIndexed { it, c ->
        when (c) {
            from -> set(it, to)
            to -> set(it, from)
        }
    }

    fun CharArray.rotate(n: Int) {
        CharArray(size) { get((it - n).mod(size)) }.copyInto(this)
    }

    val operations = mapOf<Regex, CharArray.(MatchResult.Destructured) -> Unit>(
        """swap position (\d) with position (\d)""".toRegex() to { (from, to) ->
            swap(from.toInt(), to.toInt())
        },
        """swap letter ([a-z]) with letter ([a-z])""".toRegex() to { (from, to) ->
            swap(from[0], to[0])
        },
        """rotate (left|right) (\d+) steps?""".toRegex() to { (direction, steps) ->
            if (direction[0] == 'l') rotate(-steps.toInt())
            else rotate(steps.toInt())
        },
        """rotate based on position of letter ([a-z])""".toRegex() to { (letter) ->
            val idx = indexOf(letter[0])
            rotate(idx + if (idx >= 4) 2 else 1)
        },
        """reverse positions (\d) through (\d)""".toRegex() to { (start, end) ->
            val ofs = start.toInt()
            concatToString(ofs, end.toInt().inc()).reversed()
                .forEachIndexed { idx, c -> set(ofs + idx, c) }
        },
        """move position (\d) to position (\d)""".toRegex() to { (from, to) ->
            LinkedList(toList()).apply { add(to.toInt(), removeAt(from.toInt())) }
                .forEachIndexed { idx, c -> set(idx, c) }
        },
    )

    fun CharArray.scramble(instructions: List<String>) = apply {
        instructions.forEach { line ->
            operations.firstNotNullOfOrNull { (pattern, operation) ->
                pattern.matchEntire(line)?.let { operation(it.destructured) }
            } ?: error("No operation for [$line]")
        }
    }

    fun PuzzleInput.scramble(passcode: String) = passcode.toCharArray().scramble(lines).concatToString()

    part1 {
        scramble("abcdefgh")
    }

    fun String.permutations(): Sequence<String> = sequence {
        if (isEmpty()) yield(this@permutations)
        indices.forEach { i ->
            yieldAll(removeRange(i, i + 1).permutations().map { it + get(i) })
        }
    }

    part2 {
        val scrambled = "fbgdceah"
        scrambled.permutations()
            .first { scramble(it) == scrambled }
    }
})


fun main() = solveDay(
    21,
//    input = InputProvider.raw(
//        """
//        swap position 4 with position 0
//        swap letter d with letter b
//        reverse positions 0 through 4
//        rotate left 1 step
//        move position 1 to position 4
//        move position 3 to position 0
//        rotate based on position of letter b
//        rotate based on position of letter d
//    """.trimIndent()
//    )
)
