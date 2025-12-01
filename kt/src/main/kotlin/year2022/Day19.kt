package year2022

import aok.InputProvider
import aok.PuzzleInput
import aok.lines
import aoksp.AoKSolution
import arrow.fx.coroutines.parMapUnordered
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.reduce
import java.util.PriorityQueue

fun main(): Unit = solveDay(day = 19, input = InputProvider.Example)

@AoKSolution
object Day19 {
    private data class Blueprint(
        val id: Int,
        val oreRobotOreCost: Int,
        val clayRobotOreCost: Int,
        val obsidianRobotOreCost: Int,
        val obsidianRobotClayCost: Int,
        val geodeRobotOreCost: Int,
        val geodeRobotObsidianCost: Int,
    ) {
        val maxOreCost = maxOf(oreRobotOreCost, clayRobotOreCost, obsidianRobotOreCost, geodeRobotOreCost)
    }

    context(_: PuzzleInput)
    private fun parseBlueprints() = lines.map {
        it.split(' ', ':').mapNotNull(String::toIntOrNull).let { v ->
            Blueprint(v[0], v[1], v[2], v[3], v[4], v[5], v[6])
        }
    }

    context(_: PuzzleInput)
    suspend fun part1() = parseBlueprints().asFlow()
        .parMapUnordered { it.id * it.maxGeodes() }
        .reduce(Int::plus)

    context(_: PuzzleInput)
    suspend fun part2() = parseBlueprints().take(3).asFlow()
        .parMapUnordered { it.maxGeodes(32) }
        .reduce(Int::times)

    private fun Blueprint.maxGeodes(minutes: Int = 24): Int {
        data class Resources(val ore: Int, val clay: Int, val obsidian: Int, val geode: Int)
        data class State(
            val elapsed: Int = 0,
            val robots: Resources = Resources(1, 0, 0, 0),
            val resources: Resources = Resources(0, 0, 0, 0),
            val order: List<Resources> = emptyList()
        ) {
            val remaining = minutes - elapsed
            val potential = resources.geode + (robots.geode * remaining) + (remaining * (remaining + 1)) / 2
        }

        fun State.tick(time: Int = 1) = copy(
            elapsed = elapsed + time,
            resources = Resources(
                ore = resources.ore + robots.ore * time,
                clay = resources.clay + robots.clay * time,
                obsidian = resources.obsidian + robots.obsidian * time,
                geode = resources.geode + robots.geode * time
            )
        )

        fun State.tick(
            time: Int = 1,
            buildOre: Int = 0,
            buildClay: Int = 0,
            buildObsidian: Int = 0,
            buildGeode: Int = 0,
        ) = State(
            elapsed = elapsed + time,
            robots = Resources(
                robots.ore + buildOre,
                robots.clay + buildClay,
                robots.obsidian + buildObsidian,
                robots.geode + buildGeode
            ),
            resources = Resources(
                ore = resources.ore + robots.ore * time
                        - buildOre * oreRobotOreCost
                        - buildClay * clayRobotOreCost
                        - buildObsidian * obsidianRobotOreCost
                        - buildGeode * geodeRobotOreCost,
                clay = resources.clay + robots.clay * time
                        - buildObsidian * obsidianRobotClayCost,
                obsidian = resources.obsidian + robots.obsidian * time
                        - buildGeode * geodeRobotObsidianCost,
                geode = resources.geode + robots.geode * time
            ),
            order = order + Resources(
                buildOre,
                buildClay,
                buildObsidian,
                buildGeode
            ),
        )

        infix fun Int.timeToProduce(amount: Int) = amount / this + (if (amount % this == 0) 0 else 1)

        fun State.buildGeode(): State? = if (robots.obsidian > 0) {
            val oreNeeded = maxOf(0, geodeRobotOreCost - resources.ore)
            val obsNeeded = maxOf(0, geodeRobotObsidianCost - resources.obsidian)
            val timeToBuild = maxOf(robots.ore timeToProduce oreNeeded, robots.obsidian timeToProduce obsNeeded) + 1
            if (timeToBuild < remaining) tick(timeToBuild, buildGeode = 1)
            else null
        } else null

        fun State.buildObsidian(): State? = if (robots.clay > 0 && robots.obsidian < geodeRobotObsidianCost) {
            val oreNeeded = maxOf(0, obsidianRobotOreCost - resources.ore)
            val clayNeeded = maxOf(0, obsidianRobotClayCost - resources.clay)
            val timeToBuild = maxOf(robots.ore timeToProduce oreNeeded, robots.clay timeToProduce clayNeeded) + 1
            if (timeToBuild < remaining) tick(timeToBuild, buildObsidian = 1) else null
        } else null

        fun State.buildClay(): State? = if (robots.clay < obsidianRobotClayCost) {
            val oreNeededForClay = maxOf(0, clayRobotOreCost - resources.ore)
            val timeToBuildClay = (robots.ore timeToProduce oreNeededForClay) + 1
            if (timeToBuildClay < remaining) tick(timeToBuildClay, buildClay = 1) else null
        } else null

        fun State.buildOre(): State? = if (robots.ore < maxOreCost) {
            val oreNeededForOre = maxOf(0, oreRobotOreCost - resources.ore)
            val timeToBuildOre = (robots.ore timeToProduce oreNeededForOre) + 1
            if (timeToBuildOre < remaining) tick(timeToBuildOre, buildOre = 1) else null
        } else null

        fun State.idle() = tick(remaining)

        fun State.branches() =
            listOfNotNull(idle(), buildGeode(), buildObsidian(), buildClay(), buildOre())

        val robotMap = mutableMapOf<Resources, Int>()
        fun earliestRobotsSeen(robots: Resources, elapsed: Int) = elapsed <= robotMap
            .compute(robots) { _, p -> if (p == null) elapsed else minOf(p, elapsed) }!!

        val stateComp = compareBy<State>({ it.resources.geode })//, {-(it.robots.geode + it.robots.clay + it.robots.ore + it.robots.obsidian)})
        var maxState = State()
        val queue = PriorityQueue(compareByDescending(State::potential)).also { it += State() }
        while (queue.isNotEmpty()) {
            val next = queue.poll()
            when {
                next.remaining == 0 ->
                    if (stateComp.compare(maxState, next) < 0) maxState = next

                earliestRobotsSeen(next.robots, next.elapsed) && next.potential >= maxState.resources.geode ->
                    queue += next.branches()
            }
        }
        println("${this.id}: ${maxState.order.map { if(it.ore > 0) "ore" else if (it.clay > 0) "clay" else if(it.obsidian > 0) "obsidian" else "geode" }} ${maxState.resources}")
        return maxState.resources.geode
    }
}
