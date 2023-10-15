package year2018

import aok.PuzDSL
import aoksp.AoKSolution
import year2018.Day20.navMap
import year2018.Day20.print
import year2018.Day20.roomDistances

fun main(): Unit = solveDay(
    20,
    warmup = aok.Warmup.iterations(30), runs = 50,
//    input = aok.InputProvider.raw(
//        "^N(E|W)N\$"
//        "^NE(EE|S(SW|EESEENE(EE|SEEEN(E(N|ES)|N)\$"
//        "^ENWWW(NEEE|SSE(EE|N))\$"
//        "^ESSWWN(E|NNENN(EESS(WNSE|)SSS|WWWSSSSE(SW|NNNE)))\$"
//    )
)

@AoKSolution
object Day20 : PuzDSL({

    fun Path.toDoors(from: Room = Room.Zero) = buildSet {
        DeepRecursiveFunction<Pair<Room, Path>, Set<Room>> { (room, path) ->
            when (path) {
                is Path.Simple -> {
                    var current = room
                    for (move in path.route) {
                        val next = when (move) {
                            'N' -> current.north
                            'E' -> current.east
                            'S' -> current.south
                            'W' -> current.west
                            else -> error(move)
                        }
                        add(Door(current, next))
                        current = next
                    }
                    setOf(current)
                }

                is Path.Branch -> buildSet {
                    path.paths.forEach {
                        addAll(callRecursive(room to it))
                    }
                }

                is Path.Chain -> path.paths.fold(setOf(room)) { starts, nextPath ->
                    buildSet {
                        starts.forEach {
                            addAll(callRecursive(it to nextPath))
                        }
                    }
                }
            }
        }(from to this@toDoors)
    }

    fun String.doors(): Set<Door> {
        val tokens = Regex("[NESW]+|[()|]").findAll(this).map(MatchResult::value).toList()

        fun Path.append(path: Path) {
            when (this) {
                is Path.Simple -> error("unsupported")
                is Path.Chain -> paths.add(path)
                is Path.Branch -> paths.add(path)
            }
        }

        val root = Path.Chain()
        val stack = ArrayDeque<Path>(listOf(root))
        fun addcont(path: Path) {
            stack.first().append(path)
            stack.addFirst(path)
        }
        for (token in tokens) {
            when (token) {
                "(" -> {
                    addcont(Path.Branch())
                    addcont(Path.Chain())
                }

                ")" -> {
                    stack.removeFirst() // chain
                    stack.removeFirst() // branch
                }

                "|" -> {
                    stack.removeFirst() // chain
                    addcont(Path.Chain()) // start new chain
                }

                "^", "$" -> {}
                else -> stack.first().append(Path.Simple(token))
            }
        }
        return root.toDoors()
    }

    part1 {
        val map = input.doors().navMap()
        map.print(Room.Zero)
        roomDistances(map).maxOf { it.value }
    }

    part2 {
        val map = input.doors().navMap()
        roomDistances(map).count { it.value >= 1000 }
    }
}) {
    data class Room(val x: Int, val y: Int) {
        override fun toString() = "R{$x,$y}"
        operator fun contains(door: Door) = this == door.a || this == door.b

        val north by lazy { copy(y = y - 1) }
        val east by lazy { copy(x = x + 1) }
        val south by lazy { copy(y = y + 1) }
        val west by lazy { copy(x = x - 1) }

        companion object {
            val Zero = Room(0, 0)
        }
    }

    sealed interface Path {
        data class Simple(val route: String) : Path
        data class Branch(val paths: MutableList<Path> = mutableListOf()) : Path
        data class Chain(val paths: MutableList<Path> = mutableListOf()) : Path
    }

    sealed interface Door {
        val a: Room
        val b: Room

        private data class Impl(override val a: Room, override val b: Room) : Door {
            override fun toString() = "$a<->$b"
        }

        companion object {
            private val roomCompare = compareBy(Room::y, Room::x)
            operator fun invoke(a: Room, b: Room): Door {
                val min = minOf(a, b, roomCompare)
                val max = if (a == min) b else a
                return Impl(min, max)
            }
        }
    }

    private fun Set<Door>.navMap() = sequence {
        for (door in this@navMap) {
            yield(door.a to door.b)
            yield(door.b to door.a)
        }
    }.groupBy({ (from) -> from }) { (_, to) -> to }

    private fun Map<Room, List<Room>>.print(vararg highlight: Room, glyphs: String = " ╵╶╰╷│╭├╴╯─┴╮┤┬┼") {
        mapValues { (a, bs) ->
            bs.fold(0) { n, b ->
                n or when (b) {
                    a.north -> 0b0001
                    a.east -> 0b0010
                    a.south -> 0b0100
                    a.west -> 0b1000
                    else -> error("")
                }
            }
        }.let { map ->
            val xs = map.keys.minOf { it.x }..map.keys.maxOf { it.x }
            val ys = map.keys.minOf { it.y }..map.keys.maxOf { it.y }
            for (y in ys) {
                for (x in xs) {
                    val room = Room(x, y)
                    if (room in highlight) print("\u001b[1;31m")
                    print(glyphs[map[room] ?: 0])
                    if (room in highlight) print("\u001b[0m")
                }
                println()
            }
        }
    }

    private fun roomDistances(map: Map<Room, List<Room>>) = buildMap {
        val queue = ArrayDeque(listOf(Room.Zero))
        while (queue.isNotEmpty()) {
            val room = queue.removeFirst()
            for (neighbour in map[room].orEmpty()) {
                if (!contains(neighbour)) {
                    queue += neighbour
                    set(neighbour, (get(room) ?: 0) + 1)
                }
            }
        }
    }
}

