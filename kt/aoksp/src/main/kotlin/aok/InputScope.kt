package aok

import java.io.InputStream

sealed interface PuzzleInput {
    val inputStream: InputStream
    val input: String
    val lines: List<String>
    val lineSeq: Sequence<String>
    companion object {
        fun of(input: String): PuzzleInput = Impl(input)
    }
    private data class Impl(override val input: String) : PuzzleInput {
        override val inputStream: InputStream
            get() = input.byteInputStream()
        override val lines = input.trim().lines()
        override val lineSeq = lines.asSequence()
    }
}