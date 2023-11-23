package year2016

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day25 : PuzDSL({
    /*
     * This assumes the assembunny program is as follows:
     *
     *     cpy a d     # d = a
     *     cpy 7 c     # \
     *     cpy 362 b   #  |
     *     inc d       #  |
     *     dec b       #  | d += input + (7 * 362)
     *     jnz b -2    #  |
     *     dec c       #  |
     *     jnz c -5    #  |
     *     cpy d a     # /
     *     jnz 0 0     # noop
     *     cpy a b     # - move a to b
     *     cpy 0 a     #/
     *     cpy 2 c     # c = 2
     *     jnz b 2     # if(b == 0) goto @blah
     *     jnz 1 6     # /
     *     dec b       #
     *     dec c
     *     jnz c -4
     *     inc a
     *     jnz 1 -7
     *     cpy 2 b     # <- else {
     *     jnz c 2
     *     jnz 1 4
     *     dec b
     *     dec c
     *     jnz 1 -4
     *     jnz 0 0
     *     out b
     *     jnz a -19
     *     jnz 1 -21
     */
    part1 {
        0xAAAA - input.splitToSequence(' ').mapNotNull(String::toIntOrNull).take(2).toList().let { (a, b) -> a * b }
    }
})

fun main() = solveDay(
    25,
)
