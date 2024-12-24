package year2024

import aok.PuzzleInput
import aok.checkAll
import aok.solveAll
import aok.warmup
import aoksp.AoKSolution
import utils.forEachCharIndexed

@AoKSolution
object Day15 {

    context(PuzzleInput) fun part1() = solve { walls, boxes, robot, moves ->
        moves.fold(robot to boxes) { (robot, boxes), m ->
            val nextRobot = robot + m
            if (nextRobot in walls) robot to boxes
            else if (nextRobot in boxes) {
                var nextspace = nextRobot + m
                while (nextspace in boxes) nextspace += m
                if (nextspace in walls) robot to boxes
                else nextRobot to (boxes - nextRobot + nextspace)
            } else nextRobot to boxes
        }.second.sumOf { it.y * 100 + it.x }
    }

    context(PuzzleInput) fun part2() = solve { w, b, r, moves ->
        val walls = buildSet {
            for ((x, y) in w) {
                add(Point(x * 2, y))
                add(Point(x * 2 + 1, y))
            }
        }
        val boxes = buildSet {
            for ((x, y) in b) add(Point(x * 2, y))
        }
        val robot = Point(r.x * 2, r.y)

        moves.fold(robot to boxes) { (robot, boxes), move ->
            val nextRobot = robot + move
            if (nextRobot in walls) return@fold robot to boxes // no move

            val boxesToMove = buildSet {
                val frontier = mutableListOf<Point>()

                fun include(box: Point) {
                    if (add(box)) frontier.add(box)
                }

                if (nextRobot in boxes) include(nextRobot)
                else if (nextRobot + Direction.Left in boxes) include(nextRobot + Direction.Left)

                while (frontier.isNotEmpty()) {
                    val nextBox = frontier.removeFirst()
                    val left = nextBox + move
                    val right = nextBox + Direction.Right + move

                    if (left in walls || right in walls) return@fold robot to boxes
                    if (left in boxes) include(left)
                    if (left + Direction.Left in boxes) include(left + Direction.Left)
                    if (right in boxes) include(right)
                }
            }.intersect(boxes)

            val nextBoxes = (boxes - boxesToMove + boxesToMove.map { it + move })
            nextRobot to nextBoxes
        }.second.sumOf { it.y * 100 + it.x }
    }

    fun <R> PuzzleInput.solve(action: (walls: Set<Point>, boxes: Set<Point>, robot: Point, moves: List<Direction>) -> R): R {
        val (grid, instructions) = input.split("\n\n")

        val walls = mutableSetOf<Point>()
        val boxes = mutableSetOf<Point>()
        lateinit var robot: Point

        grid.lines().forEachCharIndexed { x, y, c ->
            when (c) {
                '#' -> walls.add(Point(x, y))
                'O' -> boxes.add(Point(x, y))
                '@' -> robot = Point(x, y)
            }
        }

        val moves = instructions.mapNotNull {
            when (it) {
                'v' -> Direction.Down
                '^' -> Direction.Up
                '<' -> Direction.Left
                '>' -> Direction.Right
                else -> null
            }
        }

        return action(walls, boxes, robot, moves)
    }

    enum class Direction { Left, Right, Up, Down }
    data class Point(val x: Int, val y: Int) {
        operator fun plus(dir: Direction) = when (dir) {
            Direction.Up -> copy(y = y - 1)
            Direction.Down -> copy(y = y + 1)
            Direction.Left -> copy(x = x - 1)
            Direction.Right -> copy(x = x + 1)
        }
    }
}

fun main() {
    queryDay(15)
        .checkAll(part1 = 2028, input = {
            """
            ########
            #..O.O.#
            ##@.O..#
            #...O..#
            #.#.O..#
            #...O..#
            #......#
            ########

            <^^>>>vv<v>>v<<
        """.trimIndent()
        })
        .checkAll(part2 = 618, input = {
            """
            #######
            #...#.#
            #.....#
            #..OO@#
            #..O..#
            #.....#
            #######

            <vv<<^^<<^^
        """.trimIndent()
        })
        .checkAll(
            10092, 9021,
            true, {
                """
                ##########
                #..O..O.O#
                #......O.#
                #.OO..O.O#
                #..O@..O.#
                #O#..O...#
                #O..O..O.#
                #.OO.O.OO#
                #....O...#
                ##########

                <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
                vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
                ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
                <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
                ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
                ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
                >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
                <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
                ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
                v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
            """.trimIndent()
            })
        .warmup(30)
        .solveAll(5)
}