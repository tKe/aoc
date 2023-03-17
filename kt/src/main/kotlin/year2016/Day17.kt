package year2016

import aok.PuzDSL
import aoksp.AoKSolution
import java.security.MessageDigest
import java.util.HexFormat

@AoKSolution
object Day17 : PuzDSL({
    val md5 = MessageDigest.getInstance("MD5")
    fun String.md5() = md5.digest(toByteArray()).let(HexFormat.of()::formatHex)

    fun paths(passcode: String) = sequence {
        fun openDoors(path: String) =
            "$passcode$path".md5().zip("UDLR") { c, d -> d.takeIf { c >= 'b' } }.filterNotNull()

        infix fun Pair<Int, Int>.move(direction: Char) = when(direction) {
            'D' -> first to second + 1
            'U' -> first to second - 1
            'L' -> first - 1 to second
            'R' -> first + 1 to second
            else -> error("invalid direction '$direction'")
        }

        fun Pair<Int, Int>.isValid() = first in 0..3 && second in 0..3
        fun Pair<Int, Int>.isEnd() = first == 3 && second == 3
        fun Pair<Int, Int>.canMove(direction: Char) = move(direction).isValid()

        fun location(path: String) = path.fold(0 to 0, Pair<Int, Int>::move)

        val queue = ArrayDeque(listOf(""))
        while(queue.isNotEmpty()) {
            val path = queue.removeFirst()
            val location = location(path)
            for(direction in openDoors(path)) {
                val nextLocation = location move direction
                val nextPath = path + direction
                if(nextLocation.isEnd()) yield(nextPath)
                else if(nextLocation.isValid()) queue += nextPath
            }
        }
    }

    part1 {
        paths(input.trim()).first()
    }

    part2 {
        paths(input.trim()).last().length
    }
})

fun main() = solveDay(
    17,
//    input = InputProvider.raw("10000")
)
