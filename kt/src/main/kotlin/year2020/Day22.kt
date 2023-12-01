package year2020

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(22)

@AoKSolution
object Day22 : PuzDSL({
    val parse = parser {
        input.split("\n\n")
            .map { it.lines().drop(1).map(String::toInt) }
            .let { (a, b) -> Game(a, b) }
    }


    part1(parse) {
        it.play { a, b -> if (a > b) Player.A else Player.B }.score
    }

    part2(parse) { game ->
        DeepRecursiveFunction<Game, Game.Outcome> { next ->
            next.play { a, b ->
                when {
                    playerA.size < a || playerB.size < b -> if (a > b) Player.A else Player.B
                    else -> callRecursive(Game(playerA.take(a), playerB.take(b))).winner
                }
            }
        }(game).score
    }
}) {
    fun List<Int>.score() = asReversed().foldIndexed(0L) { idx, acc, i -> acc + i * (idx + 1) }
    enum class Player { A, B }
    data class Game(val playerA: List<Int>, val playerB: List<Int>) {
        data class Outcome(val score: Long, val winner: Player)

        inline fun play(winner: Game.(Int, Int) -> Player): Outcome {
            val deckA = ArrayDeque(playerA)
            val deckB = ArrayDeque(playerB)
            val played = mutableSetOf<Pair<List<Int>, List<Int>>>()
            while (deckA.isNotEmpty() && deckB.isNotEmpty()) {
                if (!played.add(deckA.toList() to deckB.toList())) return Outcome(deckA.score(), Player.A)
                val cardA = deckA.removeFirst()
                val cardB = deckB.removeFirst()
                when (Game(deckA, deckB).winner(cardA, cardB)) {
                    Player.A -> deckA += listOf(cardA, cardB)
                    Player.B -> deckB += listOf(cardB, cardA)
                }
            }
            return when {
                deckA.isNotEmpty() -> Outcome(deckA.score(), Player.A)
                else -> Outcome(deckB.score(), Player.B)
            }
        }
    }
}