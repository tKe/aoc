package blah

import InputScope

interface Solution<Input, Part1, Part2> {
    context(InputScope)
    fun prepareInput(): Input

    fun part1(input: Input): Part1

    fun part2(input: Input): Part2
}
