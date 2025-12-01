package year2018

import aok.PuzDSL
import aok.Warmup
import aoksp.AoKSolution
import year2018.Day15.readOrder

fun main(): Unit = solveDay(
    15,
    warmup = Warmup.iterations(3), runs = 5,
//        input = aok.InputProvider.raw("""
//#######
//#.G...#
//#...EG#
//#.#.#G#
//#..G#E#
//#.....#
//#######
//        """.trimIndent()),
)

@AoKSolution
object Day15 : PuzDSL({
    val parse = parser {
        val units = mutableListOf<Combatant>()
        val walls = mutableSetOf<Int2>()
        lines.forEachIndexed { y: Int, s: String ->
            s.forEachIndexed { x, c ->
                when (c) {
                    '#' -> walls += Int2(x, y)
                    'G' -> units += Goblin(Int2(x, y))
                    'E' -> units += Elf(Int2(x, y))
                }
            }
        }
        Cavern(walls) to units
    }

    fun List<Combatant>.battleIn(cavern: Cavern) = sequence {
        with(cavern) {
            val remainingCombatants = toMutableList()
            var rounds = 0
            while (true) {
                remainingCombatants.sortWith(compareBy(readOrder, Combatant::pos))
                for (combatant in remainingCombatants.toList()) {
                    combatant.turn(remainingCombatants)
                }
                remainingCombatants.removeAll { it.hp <= 0 }
                yield(remainingCombatants)
                if (remainingCombatants.all { it is Goblin } || remainingCombatants.all { it is Elf }) {
                    break
                }
                rounds++
            }
        }
    }.withIndex().last()

    part1(parse) { (cavern, units) ->
        val (rounds, alive) = units.battleIn(cavern)
        rounds * alive.sumOf(Combatant::hp)
    }

    part2(parse) { (cavern, units) ->
        for (ap in 4..200) {
            val combatants = units.map {
                when (it) {
                    is Elf -> Elf(it.pos)
                    is Goblin -> Goblin(it.pos)
                }
            }
            val elves = combatants.filterIsInstance<Elf>()
            elves.forEach { it.ap = ap }
            val (rounds, _) = combatants.battleIn(cavern)
            // println("ap:$ap, rounds:$rounds, deadElves:${elves.count { it.hp <= 0 }}")
            // with(cavern) { debug(elves) }
            if (elves.count { it.hp <= 0 } == 0) {
                return@part2 rounds * elves.sumOf(Combatant::hp)
            }
        }
    }

}) {
    data class Int2(val x: Int, val y: Int)

    val readOrder = compareBy(Int2::y, Int2::x)

    @JvmInline
    value class Cavern(val walls: Set<Int2>)

    sealed class Combatant(var pos: Int2, var ap: Int = 3, var hp: Int = 200) {
        open fun isEnemy(combatant: Combatant) = this::class != combatant::class

        context(cavern: Cavern)
        fun turn(combatants: List<Combatant>) {
            if (hp <= 0) return
            moveTowardEnemy(combatants)
            val enemy = combatants
                .filter { it.hp > 0 && it.pos in pos.neighbours && isEnemy(it) }
                .minWithOrNull(compareBy(Combatant::hp).thenBy(readOrder, Combatant::pos))
            if (enemy != null) enemy.hp -= ap
        }

        context(cavern: Cavern)
        private val Int2.neighbours
            get() = sequenceOf(
                copy(y = y - 1),
                copy(x = x - 1),
                copy(x = x + 1),
                copy(y = y + 1),
            ).filterNot(cavern.walls::contains)

        context(cavern: Cavern)
        private fun moveTowardEnemy(combatants: List<Combatant>) {
            val (enemies, occupied) = combatantPositions(combatants)
            fun Int2.navigableNeighbours() = neighbours.filterNot(occupied::contains)

            if (pos.neighbours.any(enemies::contains)) return // don't move
            val targets = enemies.flatMapTo(mutableSetOf(), Int2::navigableNeighbours)
            pos.neighbours.firstOrNull(targets::contains)?.let {
                pos = it
                return
            }

            data class Route(val start: Int2, val end: Int2 = start, val length: Int = 1)

            operator fun Route.plus(pos: Int2) = copy(end = pos, length = length + 1)

            val queue = ArrayDeque<Route>()
            pos.navigableNeighbours().mapTo(queue, ::Route)
            val visited = mutableSetOf(pos)
            var distanceLimit = Int.MAX_VALUE
            val routes = mutableSetOf<Route>()
            while (queue.isNotEmpty()) {
                val route = queue.removeFirst()
                if (route.length >= distanceLimit) break
                for (neighbour in route.end.navigableNeighbours()) {
                    val routeToNeighbour = route + neighbour
                    if (neighbour in targets) {
                        distanceLimit = routeToNeighbour.length
                        routes += routeToNeighbour
                    } else if (visited.add(neighbour)) queue += routeToNeighbour
                }
            }

            if (routes.isNotEmpty()) {
                pos = routes.minWith(
                    compareBy(readOrder, Route::end).thenBy(readOrder, Route::start)
                ).start
            }
        }

        private fun combatantPositions(combatants: List<Combatant>): Pair<MutableSet<Int2>, MutableSet<Int2>> {
            val enemies = mutableSetOf<Int2>()
            val alive = mutableSetOf<Int2>()
            for (combatant in combatants) if (combatant.hp > 0) {
                alive += combatant.pos
                if (isEnemy(combatant)) enemies += combatant.pos
            }
            return Pair(enemies, alive)
        }
    }

    class Goblin(pos: Int2) : Combatant(pos)

    class Elf(pos: Int2) : Combatant(pos)

}
