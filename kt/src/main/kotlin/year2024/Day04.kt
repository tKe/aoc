package year2024

import aok.PuzDSL
import aok.Warmup
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day04 : PuzDSL({
    part1 {
        fun isMAS(x: Int, y: Int, dx: Int, dy: Int) =
            lines[y + dy + dy][x + dx + dx] == 'S' &&
                lines[y + dy][x + dx] == 'A' &&
                lines[y][x] == 'M'

        var count = 0
        lines.forEachIndexed { y, s ->
            for (x in s.indices) if (s[x] == 'X') {
                for (dx in -1..1) if (x + 3 * dx in s.indices) {
                    for (dy in -1..1) if (y + 3 * dy in lines.indices) {
                        if (isMAS(x + dx, y + dy, dx, dy)) count++
                    }
                }
            }
        }
        count
    }

    part2 {
        fun isMS(a: String, b: String, x: Int) = when (a[x - 1]) {
            'M' -> 'S' == b[x + 1]
            'S' -> 'M' == b[x + 1]
            else -> false
        }

        var count = 0
        lines.windowed(3) { (up, line, down) ->
            for (x in 1..<line.lastIndex) if (line[x] == 'A') {
                if (isMS(up, down, x) && isMS(down, up, x)) count++
            }
        }
        count
    }
})

fun main() = solveDay(4, warmup = Warmup.allFor(5.seconds), runs = 500)