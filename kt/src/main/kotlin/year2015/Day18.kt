package year2015

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

private typealias Grid = Array<BooleanArray>

@AoKSolution
object Day18 : PuzDSL({

    fun PuzzleInput.parseGrid() = lines.map { it.map('#'::equals).toBooleanArray() }.toTypedArray()
    operator fun Grid.get(x: Int, y: Int) = getOrNull(y)?.getOrNull(x) ?: false
    operator fun Grid.set(x: Int, y: Int, value: Boolean) = get(y).set(x, value)
    fun Grid.countNeighbours(x: Int, y: Int) =
        (x - 1..x + 1).sumOf { nx ->
            (y - 1..y + 1).count { ny -> (nx != x || ny != y) && get(nx, ny) }
        }
    fun Grid.step() = Array(size) { y ->
        BooleanArray(this[0].size) { x ->
            when(countNeighbours(x, y)) {
                2 -> this[x, y]
                3 -> true
                else -> false
            }
        }
    }
    fun Grid.countLit() = sumOf { it.count { a -> a } }

    part1 {
        generateSequence(parseGrid(), Grid::step)
            .elementAt(100)
            .countLit()
    }

    part2 {
        fun Grid.lightCorners() = apply {
            this[0][0] = true
            this[0][this[0].lastIndex] = true
            this[lastIndex][0] = true
            this[lastIndex][this[0].lastIndex] = true
        }

        generateSequence(parseGrid().lightCorners()) { it.step().lightCorners() }
            .elementAt(100)
            .countLit()
    }
})

fun main() = solveDay(
    18,
//    input = InputProvider.raw("""
//        .#.#.#
//        ...##.
//        #....#
//        ..#...
//        #.#..#
//        ####..
//    """.trimIndent())
)
