package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import kotlinx.coroutines.flow.last
import year2019.Day09.IntcodeCpu
import year2019.Day09.IntcodeProgram

fun main() = solveDay(
    17,
)

@AoKSolution
object Day17 : PuzDSL({

    fun IntcodeCpu.readMap() = buildString {
        while (true) when (val interrupt = advance()) {
            is IntcodeCpu.Output -> append(interrupt.read().toInt().toChar())
            else -> break
        }
    }

    part1(IntcodeProgram) { p ->
        val vacuumRobot = p.load()
        val output = vacuumRobot.readMap().trim().lines()
        var sum = 0
        val xr = 1..<output.first().lastIndex
        for (y in 1..<output.lastIndex) {
            val row = output[y]
            for (x in xr) {
                if (row[x - 1] == '#' && row[x] == '#' && row[x + 1] == '#' && output[y - 1][x] == '#' && output[y + 1][x] == '#') {
                    sum += x * y
                }
            }
        }
        sum
    }

    data class Bot(val x: Int, val y: Int, val f: Dir)

    fun List<String>.findBot(): Bot {
        for ((y, row) in withIndex()) {
            for (x in row.indices) {
                when (row[x]) {
                    'v' -> return Bot(x, y, Dir.D)
                    '^' -> return Bot(x, y, Dir.U)
                    '<' -> return Bot(x, y, Dir.L)
                    '>' -> return Bot(x, y, Dir.R)
                    'X' -> error("Bot has been spaced!")
                }
            }
        }
        error("no bot found")
    }

    part2(IntcodeProgram) { p ->
        val map = p.load().readMap().trim().lines()
        val scaff = map.map { it.map { it != '.' }.toBooleanArray() }
        val bot = map.findBot()

        infix fun Bot.move(dir: Dir) = when (dir) {
            Dir.U -> copy(y = y - 1, f = dir)
            Dir.D -> copy(y = y + 1, f = dir)
            Dir.L -> copy(x = x - 1, f = dir)
            Dir.R -> copy(x = x + 1, f = dir)
        }

        infix fun Bot.canMove(move: Move) = when (move) {
            Move.F -> move(f)
            Move.L -> move(f.left)
            Move.R -> move(f.right)
        }.let { scaff.getOrNull(it.y)?.getOrNull(it.x) == true }

        infix fun Bot.move(move: Move): Bot = when (move) {
            Move.L -> copy(f = f.left)
            Move.R -> copy(f = f.right)
            Move.F -> move(f)
        }

        fun Bot.route() = sequence {
            var current = this@route
            routing@ while (true) {
                for (m in Move.entries) if (current canMove m) {
                    current = current move m
                    yield(m)
                    continue@routing
                }
                break@routing
            }
        }

        fun Sequence<Move>.toASCII() = sequence {
            var i = 0
            for (move in this@toASCII) {
                if (move == Move.F) {
                    i++
                } else {
                    if (i > 0) {
                        yield(i.toString())
                        i = 0
                    }
                    if (move == Move.L) yield("L")
                    if (move == Move.R) yield("R")
                }
            }
            if (i > 0) yield(i.toString())
        }.joinToString(",")

        fun String.replaceAll(vararg replacements: Pair<String, String>) = replacements
            .fold(this) { s, (f, t) -> s.replace(f, t) }

        // route will be in the form of
        //   A followed by zero or more As, followed by
        //   ,B followed by zero or more As or Bs, followed by
        //   ,C followed by zero or more As, Bs or Cs
        // where A, B and C are at most 20 chars long
        // so ... use regex with back-references...
        val compactRegex = "(.{1,20})" + // A
                """(?:,\1)*""" + // zero or more ,A
                ",(.{1,20})" + // ,B
                """(?:,(?:\1|\2))*""" + // zero or more ,(A|B)
                ",(.{1,20})" + // ,C
                """(?:,(?:\1|\2|\3))*""" // zero or more ,(A|B|C)
        fun String.compactASCII() = Regex(compactRegex).matchEntire(this)
            .let { it ?: error("no matching compaction found") }
            .let {
                val (a, b, c) = it.destructured
                val routine = it.value.replaceAll(a to "A", b to "B", c to "C")
                """
                    $routine
                    $a
                    $b
                    $c
                    n
                    
                """.trimIndent()
            }

        val compactAscii = bot.route().toASCII().compactASCII()

        p.modify { it[0] = 2 }.process(*compactAscii.map { it.code.toLong() }.toLongArray()).last()

    }
}) {
    enum class Move { F, L, R }
    enum class Dir {
        U, D, L, R;

        val left by lazy {
            when (this) {
                U -> L
                D -> R
                L -> D
                R -> U
            }
        }
        val right by lazy { left.left.left } // really lazy
    }
}