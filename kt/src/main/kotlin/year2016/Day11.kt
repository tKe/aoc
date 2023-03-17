package year2016

import aok.PuzDSL
import aok.PuzzleInput
import aok.Warmup
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day11 : PuzDSL({
    val genPattern = """a ([^ ]+) generator""".toRegex()
    val chipPattern = """a ([^- ]+)-compatible microchip""".toRegex()

    fun <R> Regex.mapAll(it: CharSequence, transform: (MatchResult.Destructured) -> R) =
        findAll(it).map { transform(it.destructured) }

    data class Floor(
        val generators: Set<String> = emptySet(),
        val microchips: Set<String> = emptySet(),
    )

    class State(
        val elevator: Int,
        val floors: List<Floor>,
    ) {
        val description by lazy {
            elevator to floors.flatMapIndexed { genFloor, (gens) ->
                gens.asSequence()
                    .map { floors.indexOfFirst { (_, chips) -> it in chips } }
                    .sorted().map { genFloor to it }
            }
        }
    }

    fun Floor.isEmpty() = generators.isEmpty() && microchips.isEmpty()

    fun PuzzleInput.parseState() = State(
        elevator = 0,
        floors = lines.map {
            Floor(
                generators = genPattern.mapAll(it) { (s) -> s }.toSet(),
                microchips = chipPattern.mapAll(it) { (s) -> s }.toSet()
            )
        }
    )

    fun <T> Set<T>.subsets(maxSize: Int = 2): Sequence<Set<T>> = sequence {
        when {
            maxSize == 1 -> yieldAll(map(::setOf))
            maxSize > 1 -> forEach {
                (this@subsets - it).subsets(maxSize - 1).forEach { s -> yield(s + it) }
            }
        }
        yield(emptySet())
    }

    fun State.isValid() = floors.all { (gens, chips) ->
        (chips - gens).isEmpty() || gens.isEmpty()
    }

    fun State.options() = sequence {
        val (gens, chips) = floors[elevator]
        val firstPopulated = floors.indices.dropWhile { floors[it].isEmpty() }.first()
        listOf(elevator + 1, elevator - 1)
            .filter((firstPopulated..floors.lastIndex)::contains)
            .forEach { destination ->
                gens.subsets(maxSize = 2).forEach { selectedGens ->
                    chips.subsets(maxSize = 2 - selectedGens.size).forEach { selectedChips ->
                        if (selectedChips.isNotEmpty() || selectedGens.isNotEmpty()) {
                            State(destination, floors.mapIndexed { index, (g, c) ->
                                when (index) {
                                    elevator -> Floor(g - selectedGens, c - selectedChips)
                                    destination -> Floor(g + selectedGens, c + selectedChips)
                                    else -> Floor(g, c)
                                }
                            }).takeIf(State::isValid)?.let { yield(it) }
                        }
                    }
                }
            }
    }

    fun State.complete() = floors.take(3).all(Floor::isEmpty)

    fun State.minimalSteps(): Int {
        val queue = ArrayDeque(listOf(this to 0))
        val visited = mutableSetOf<Any>()
        while (queue.isNotEmpty()) {
            val (node, dist) = queue.removeFirst()
            for (neighbour in node.options()) {
                if (neighbour.complete()) return dist + 1
                if (visited.add(neighbour.description)) queue += neighbour to dist + 1
            }
        }
        return -1
    }

    part1 {
        parseState().minimalSteps()
    }

    part2 {
        parseState().let {
            val extras = setOf("elerium", "dilithium")
            val (gens, chips) = it.floors.first()
            val firstFloor = Floor(gens + extras, chips + extras)
            State(0, listOf(firstFloor) + it.floors.drop(1))
        }.minimalSteps()
    }
})

fun main() = solveDay(
    11,
    warmup = Warmup.eachFor(5.seconds)
//    input = InputProvider.raw(
//        """
//        The first floor contains a hydrogen-compatible microchip and a lithium-compatible microchip.
//        The second floor contains a hydrogen generator.
//        The third floor contains a lithium generator.
//        The fourth floor contains nothing relevant.
//    """.trimIndent()
//    )
)
