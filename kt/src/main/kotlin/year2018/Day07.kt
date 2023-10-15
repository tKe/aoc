package year2018

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day07 : PuzDSL({
    val parseDeps = parser {
        val deps = lines.map { it.split(" ").mapNotNull(String::singleOrNull) }
        deps.associate { (dep) -> dep to emptyList<Char>() } +
                deps.groupBy({ (_, step) -> step }) { (dep, _) -> dep }
    }

    fun Map<Char, List<Char>>.completeStep(step: Char) =
            mapValues { it.value.filter((keys - step)::contains) } - step

    fun Map<Char, List<Char>>.nextStep(skip: (Char) -> Boolean = { false }) =
            mapNotNull { (step, deps) -> step.takeIf { deps.isEmpty() } }
                    .filterNot(skip)
                    .minOrNull()

    part1(parseDeps) { deps ->
        buildString(deps.size) {
            var remaining = deps
            while (remaining.isNotEmpty()) {
                val selected = remaining.nextStep() ?: error("no next step")
                remaining = remaining.completeStep(selected).also { append(selected) }
            }
        }
    }

    val workers = 5
    val baseDuration = 60
    part2(parseDeps) { deps ->
        data class Work(val step: Char, var remaining: Int = baseDuration + (step - 'A' + 1))
        sequence {
            var rem = deps
            val work = mutableListOf<Work>()
            while (rem.isNotEmpty()) {
                while (work.size < workers) {
                    work += rem.nextStep { work.any { (step) -> step == it } }
                            ?.let(::Work)
                            ?: break
                }
                val nextTick = work.minOf { it.remaining }
                work.removeAll {
                    it.remaining -= nextTick
                    (it.remaining <= 0).also { done ->
                        if(done) rem = rem.completeStep(it.step)
                    }
                }
                yield(nextTick)
            }
        }.sum()
    }
})

fun main(): Unit = solveDay(
        7,
//        input = aok.InputProvider.raw(
//                """
//        Step C must be finished before step A can begin.
//        Step C must be finished before step F can begin.
//        Step A must be finished before step B can begin.
//        Step A must be finished before step D can begin.
//        Step B must be finished before step E can begin.
//        Step D must be finished before step E can begin.
//        Step F must be finished before step E can begin.
//        """.trimIndent()
//        ),
)
