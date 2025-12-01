package aok

sealed interface PuzKey {
    val year: Int
    val day: Int
    val variant: String

    companion object {
        fun of(year: Int, day: Int, variant: String): PuzKey = Impl(year, day, variant)
    }

    private data class Impl(override val year: Int, override val day: Int, override val variant: String) : PuzKey
}

interface Puz<out P1, out P2> : PuzKey{
    context(_: PuzzleInput) fun part1(): P1 = TODO()
    context(_: PuzzleInput) fun part2(): P2 = TODO()
}
