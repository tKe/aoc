package year2016

import aok.InputProvider
import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day19 : PuzDSL({
    part1 {
        val elfs = IntArray(input.trim().toInt()) { 1 }

        while (elfs.count { it != 0 } > 1) {
            elfs.indices.forEach {
                if (elfs[it] != 0) {
                    var nextElf = it + 1
                    while (elfs[nextElf] == 0) nextElf = (nextElf + 1) % elfs.size
                    elfs[it] += elfs[nextElf]
                    elfs[nextElf] = 0
                }
            }
        }

        elfs.indexOfFirst { it != 0 } + 1
    }

    part2 {
        data class Elf(val n: Int) {
            var prev = this
            var next = this
        }

        fun Elf.eliminate(): Elf {
            prev.next = next
            next.prev = prev
            return next
        }

        fun elfCircle(elves: Int) = Elf(1).also { elf ->
            generateSequence(elf) {
                Elf(it.n + 1).apply {
                    it.next = this
                    prev = it
                }
            }.take(elves).last().let { last ->
                last.next = elf
                elf.prev = last
            }
        }

        val elves = input.trim().toInt()
        var target = generateSequence(elfCircle(elves), Elf::next).elementAt(elves / 2)
        for(i in elves downTo 1) {
            target = target.eliminate()
            if(i % 2 == 1) target = target.next
        }
        target.n
    }
})

fun main() = solveDay(
    19,
    input = InputProvider.raw("3005290")
)
