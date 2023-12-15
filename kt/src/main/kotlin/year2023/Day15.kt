package year2023

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(
    15,
//    input = aok.InputProvider.raw("rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7")
)

@AoKSolution
object Day15 : PuzDSL({
    val parse = parser { input.split(',', '\n') }
    fun String.hash() = fold(0) { h, c -> (h + c.code) * 17 and 255 }
    part1(parse) { it.sumOf(String::hash) }
    part2(parse) { lenses ->
        val boxes = Array(256) { mutableMapOf<String, Int>() }
        for ((label, op) in lenses.map { it.split('-', '=') }) {
            when (val focal = op.toIntOrNull()) {
                null -> boxes[label.hash()] -= label
                else -> boxes[label.hash()][label] = focal
            }
        }
        boxes.withIndex().sumOf { (box, content) ->
            (box + 1) * content.values.withIndex().sumOf { (slot, focal) -> (slot + 1) * focal }
        }
    }
})
