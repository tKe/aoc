package year2019

import aok.PuzDSL
import aoksp.AoKSolution
import utils.bfsRoute
import utils.permute
import year2019.Day15.Direction
import year2019.Day15.Direction.*
import year2019.Day15.Int2
import year2019.Day21.AsciiCpu

fun main() = solveDay(
    25,
)

@AoKSolution
object Day25 : PuzDSL({

    data class Room(
        val name: String,
        val doors: List<Direction>,
        val items: List<String>,
    )

    fun AsciiCpu.Message.parse(): Room {
        val parts = value.split("\n\n").map(String::trim).filter(String::isNotEmpty)
        val room = parts.first { it.startsWith("== ") }.split("== ", " ==")[1]
        fun readList(header: String) =
            parts.firstOrNull { it.startsWith("$header\n- ") }?.split("\n- ")?.drop(1).orEmpty()
        return Room(
            name = room,
            doors = readList("Doors here lead:").map {
                when (it) {
                    "north" -> North
                    "south" -> South
                    "east" -> East
                    "west" -> West
                    else -> error(it)
                }
            },
            items = readList("Items here:")
        )
    }

    part1(Day09.IntcodeProgram) { code ->
        val shipMap = buildMap {
            data class State(
                val snapshot: Day09.IntcodeCpu.Snapshot = code.load().snapshot(),
                val loc: Int2 = Int2(0, 0)
            )

            val visited = mutableSetOf(Int2(0, 0))
            val pending = ArrayDeque(listOf(State()))
            while (pending.isNotEmpty()) {
                val next = pending.removeFirst()

                val cpu = next.snapshot.fork()
                val console = AsciiCpu(cpu)
                val message = console.read() as AsciiCpu.Message
                put(next.loc, message.parse())
                val snap = cpu.snapshot()
                for (door in message.parse().doors) {
                    val nextRoom = next.loc.move(door)
                    if (visited.add(nextRoom)) {
                        cpu.restore(snap)
                        console.write(door.name.lowercase() + "\n")
                        pending += State(cpu.snapshot(), nextRoom)
                    }
                }
            }
        }

        shipMap.onEach(::println)
        val checkpoint = shipMap.filter { it.value.name == "Security Checkpoint" }.keys.single()
        val wantedItems = shipMap.values.flatMapTo(mutableSetOf(), Room::items) - listOf(
            "molten lava",
            "escape pod",
            "photons",
            "giant electromagnet",
            "infinite loop"
        )

        data class State(
            val loc: Int2 = Int2(0, 0),
            val inv: Set<String> = emptySet(),
            val take: Set<String> = emptySet(),
            val direction: Direction? = null
        )
        val route = bfsRoute(State(), {
            loc == checkpoint
                    && inv.containsAll(wantedItems)
        }) {
            val room = shipMap[loc]!!
            val take = room.items.intersect(wantedItems)
            room.doors.asSequence().map { State(loc.move(it), (inv + take), take, it) }
        }.flatMap { state ->
            state.take.map { "take $it" } + listOfNotNull(state.direction).map { it.name.lowercase() }
        }.onEach(::println)

        val cpu = code.load()
        with(AsciiCpu(cpu)) {
            for(command in route) {
                val message = read() as AsciiCpu.Message
                println(message)
                write(command + "\n")
            }
            read()

            write("north\n")
            read()

            for(item in wantedItems) {
                write("drop $item\n")
                read()
            }
        }
        val snap = cpu.snapshot()

        val selections = wantedItems.permute().map { it.take(4).toSet() }.distinct().toSet()

        for(selection in selections) {
            val c = AsciiCpu(snap.fork())
            for(item in selection) {
                c.write("take $item\n")
                c.read()
            }
            c.write("inv\n")
            println(c.read())
            c.write("west\n")
            val read = c.read()
            if(!read.toString().contains("Alert!")) {
                println(read)
                break
            }
        }

        AsciiCpu(snap.fork()).run {
            while (isRunning) {
                val message = read().also(::println) as AsciiCpu.Message
                println(runCatching { message.parse() }.getOrElse { "--" })
                write(readln())
            }
        }
    }

    // North, North, East, West, South, South, East, South, East, South, East, West, West, East, North, West, North, West, West, North, North
    part2(Day09.IntcodeProgram) { code ->
        AsciiCpu(code.load()).run {
            while (isRunning) {
                val message = read().also(::println) as AsciiCpu.Message
                println(runCatching { message.parse() }.getOrElse { "--" })
                write(readln())
            }
        }
    }
})