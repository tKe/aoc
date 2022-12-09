package year2022

import InputScope
import solveAll
import year2022.Hand.*
import year2022.Outcome.*

sealed class Day02Puz(variant: String? = null) : Puz22Base<Int, Int>(2, variant)

private enum class Hand {
    Rock,
    Paper,
    Scissors
}

private enum class Outcome { Win, Lose, Draw }

private infix fun Hand.defeats(other: Hand) = other == beats()

private fun Hand.beats() = when (this) {
    Rock -> Scissors
    Paper -> Rock
    Scissors -> Paper
}
private fun Hand.loses() = when (this) {
    Scissors -> Rock
    Rock -> Paper
    Paper -> Scissors
}

object Day02: Day02Puz() {

    private fun Char.toPlay() = when (this) {
        'A' -> Rock
        'B' -> Paper
        'C' -> Scissors
        else -> error("not a play")
    }

    private fun Char.toOutcome() = when (this) {
        'X' -> Lose
        'Y' -> Draw
        'Z' -> Win
        else -> error("not a play")
    }

    context(InputScope)
    override fun part1() =
        lineSeq.filter { it.isNotBlank() }
            .sumOf { playRound(it[0].toPlay(), (it[2] - ('X'-'A')).toPlay()) }

    private fun playRound(theirs: Hand, ours: Hand): Int {
        val roundScore = when {
            theirs defeats ours -> 0
            ours defeats theirs -> 6
            else -> 3
        }
        val playScore = when (ours) {
            Rock -> 1
            Paper -> 2
            Scissors -> 3
        }
        return roundScore + playScore
    }

    context(InputScope)
    override fun part2() =
        lineSeq.filter { it.isNotBlank() }
            .sumOf {
                val theirs = it[0].toPlay()
                val ours = when (it[2].toOutcome()) {
                    Lose -> theirs.beats()
                    Draw -> theirs
                    Win -> theirs.loses()
                }
                playRound(theirs, ours)
            }
}

fun main() = solveAll<Day02Puz>()
