package year2016

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution

@AoKSolution
object Day04 : PuzDSL({
    val roomPattern = """([a-z-]+)-(\d+)\[([a-z]{5})]""".toRegex()
    fun PuzzleInput.validRooms() = lines
        .mapNotNull { room ->
            val (encryptedName, sectorId, checkSum) = roomPattern.matchEntire(room)?.destructured
                ?: error("invalid room: $room")

            val check = encryptedName.filter { it in 'a'..'z' }
                .groupingBy { it }.eachCount()
                .entries.sortedWith(compareBy({ -it.value }, { it.key }))
                .take(5).joinToString("") { it.key.toString() }

            if (checkSum == check) sectorId.toInt() to encryptedName else null
        }

    part1 {
        validRooms().sumOf { (sectorId) -> sectorId }
    }

    part2 {
        fun String.translate(mapper: (Char) -> Char) =
            toCharArray().apply { indices.forEach { set(it, mapper(get(it))) } }.concatToString()

        validRooms().map { (sectorId, encrypted) ->
            sectorId to encrypted.translate { if(it in 'a'..'z') 'a' + (it - 'a' + sectorId) % 26 else it }
        }.single { "northpole" in it.second }.first
    }
})


fun main() = solveDay(
    4,
)
