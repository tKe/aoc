package year2023

import aok.PuzDSL
import aoksp.AoKSolution
import utils.dijkstra
import year2023.Day17.Crucible
import year2023.Day17.Direction
import year2023.Day17.Direction.*
import java.util.*

fun main() = solveDay(
    17,
    warmup = aok.Warmup.iterations(100), runs = 30,
//    input = aok.InputProvider.Example,
)

@AoKSolution
object Day17 : PuzDSL({
    val parser = lineParser { it.map(Char::digitToInt).toIntArray() }.andThen { it.toTypedArray() }
    fun Array<IntArray>.reachedFactory(crucible: Crucible) = crucible.y == lastIndex && crucible.x == first().lastIndex
    fun Array<IntArray>.route(
        minSpeed: Int = 1, maxSpeed: Int = 3,
    ) = dijkstra(Crucible(), {
        it.speed >= minSpeed && reachedFactory(it)
    }, Crucible::heatloss) {
        suspend fun SequenceScope<Crucible>.tryMove(dir: Direction) = it.move(dir).let { next ->
            next.heatloss = it.heatloss + (getOrNull(next.y)?.getOrNull(next.x) ?: return)
            yield(next)
        }

        if (it.speed == 0) for (d in Direction.entries) tryMove(d)
        if (it.speed in 1..<maxSpeed) tryMove(it.direction)
        if (it.speed >= minSpeed) {
            tryMove(it.direction.turnLeft)
            tryMove(it.direction.turnRight)
        }
    }.heatloss

    part1(parser) { it.route() }
    part2(parser) { it.route(minSpeed = 4, maxSpeed = 10) }
}) {
    enum class Direction {
        Up, Right, Down, Left;

        val turnLeft by lazy { entries[(ordinal + 3) % entries.size] }
        val turnRight by lazy { entries[(ordinal + 1) % entries.size] }
    }

    data class Crucible(val x: Int = 0, val y: Int = 0, val direction: Direction = Up, val speed: Int = 0) {
        var heatloss = 0
        fun move(dir: Direction): Crucible = when (dir) {
            Up -> copy(y = y - 1, direction = dir, speed = if (direction == dir) speed + 1 else 1)
            Down -> copy(y = y + 1, direction = dir, speed = if (direction == dir) speed + 1 else 1)
            Left -> copy(x = x - 1, direction = dir, speed = if (direction == dir) speed + 1 else 1)
            Right -> copy(x = x + 1, direction = dir, speed = if (direction == dir) speed + 1 else 1)
        }
    }
}

@AoKSolution
object Day17IntArray : PuzDSL({
    val parser = lineParser { it.map(Char::digitToInt).toIntArray() }.andThen { it.toTypedArray() }
    fun Array<IntArray>.route(
        minSpeed: Int = 1, maxSpeed: Int = 3,
    ): Int {
        val bests = IntArray((size * first().size).shl(6)) { Int.MAX_VALUE }
        fun Crucible.idx() = (y * size + x).shl(6) + speed.shl(2) + direction.ordinal
        fun Crucible.isBest() = idx().let { raw -> (bests[raw] > heatloss).also { best -> if (best) bests[raw] = heatloss } }
        fun Crucible.isEnd() = speed >= minSpeed && y == lastIndex && x == first().lastIndex
        val pending = PriorityQueue<Crucible>(compareBy { it.heatloss })
        pending += Crucible()

        while (pending.isNotEmpty() && !pending.peek().isEnd()) {
            val current = pending.poll()
            fun tryMove(dir: Direction) = current.move(dir).let { next ->
                next.heatloss = current.heatloss + (getOrNull(next.y)?.getOrNull(next.x) ?: return)
                if (next.isBest()) pending += next
            }
            if (current.speed == 0) for (d in entries) tryMove(d)
            if (current.speed in 1..<maxSpeed) tryMove(current.direction)
            if (current.speed >= minSpeed) {
                tryMove(current.direction.turnLeft)
                tryMove(current.direction.turnRight)
            }
        }
        return pending.firstOrNull()?.heatloss ?: error("no route")
    }

    part1(parser) { it.route() }
    part2(parser) { it.route(minSpeed = 4, maxSpeed = 10) }
})
