package year2022

import aok.PuzzleInput
import aok.lineSeq
import aoksp.AoKSolution
import year2022.Hand.Paper
import year2022.Hand.Rock
import year2022.Hand.Scissors
import year2022.Outcome.Draw
import year2022.Outcome.Lose
import year2022.Outcome.Win

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

@AoKSolution
object Day02 {

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

    context(_: PuzzleInput)
    fun part1() =
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

    context(_: PuzzleInput)
    fun part2() =
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

fun main() = solveDay(2)
