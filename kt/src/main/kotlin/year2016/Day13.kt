package year2016

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day13 : PuzDSL({

    data class Coordinate(val x: Int, val y: Int)

    fun Coordinate.isOpen(officeDesignersFavouriteNumber: Int = 1358) =
        ((x * x) + (3 * x) + (2 * x * y) + y + (y * y) + officeDesignersFavouriteNumber).countOneBits() % 2 == 0

    fun Coordinate.neighbours() = sequence {
        yield(copy(x = x + 1))
        yield(copy(y = y + 1))
        if (x > 0) yield(copy(x = x - 1))
        if (y > 0) yield(copy(y = y - 1))
    }.filter(Coordinate::isOpen)

    infix fun Int.x(y: Int) = Coordinate(this, y)

    infix fun Coordinate.stepsTo(destination: Coordinate): Int {
        val pending = ArrayDeque(listOf(this to 0))
        val visited = mutableSetOf<Coordinate>()
        while (pending.isNotEmpty()) {
            val (location, distance) = pending.removeFirst()
            for (neighbour in location.neighbours()) {
                if (neighbour == destination) return distance + 1
                if (visited.add(neighbour)) pending += neighbour to distance + 1
            }
        }
        return -1
    }

    part1 {
        (1 x 1) stepsTo (31 x 39)
    }

    fun Coordinate.visitable(steps: Int = 50) = buildSet {
        add(this@visitable)
        repeat(steps) {
            flatMap(Coordinate::neighbours).filter(::add)
        }
    }


    part2 {
        (1 x 1).visitable().size
    }
})

fun main() = solveDay(
    13,
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
