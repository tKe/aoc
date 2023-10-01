package aok

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readText

sealed interface PuzzleInput {
    val input: String
    val lines: List<String>
    val lineSeq: Sequence<String>

    companion object {
        fun of(input: String): PuzzleInput = Impl(input.trimEnd())
        fun of(file: Path) = of(file.readText().trimEnd())
    }

    private data class Impl(override val input: String) : PuzzleInput {
        override val lines = input.lines()
        override val lineSeq = lines.asSequence()
    }
}

private fun findDirectoryInAncestors(name: String) = generateSequence(Paths.get(name).toAbsolutePath()) {
    it.parent?.parent?.resolve(name)
}.firstOrNull { Files.isDirectory(it) }

private val searchPaths by lazy {
    listOfNotNull(
        findDirectoryInAncestors("inputs"),
        findDirectoryInAncestors("aoc-inputs")
    ).also { require(it.isNotEmpty()) { "Could not locate input directory" } }
}

@JvmInline
value class RawInput(private val content: String) : InputProvider {
    override fun forPuzzle(year: Int, day: Int) = PuzzleInput.of(content)
}

fun interface InputProvider {
    fun forPuzzle(year: Int, day: Int): PuzzleInput

    companion object : InputProvider {
        val Example = InputProvider { year, day ->
            PuzzleInput.of(resolveInputFile(year, day, "example.txt"))
        }

        fun raw(content: String) = RawInput(content)

        override fun forPuzzle(year: Int, day: Int): PuzzleInput =
            PuzzleInput.of(resolveInputFile(year, day, "input.txt"))

        private fun resolveInputFile(year: Int, day: Int, name: String): Path {
            val path = "year-%d/day-%02d/%s".format(year, day, name)
            return searchPaths.firstNotNullOfOrNull { it.resolve(path).takeIf(Files::isReadable) }
                ?: error("No input $name for $year-$day in any of $searchPaths")
        }
    }
}

fun <R> withInput(content: String, block: context(PuzzleInput) () -> R) = PuzzleInput.of(content).run(block)
