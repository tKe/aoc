import aok.PuzzleInput
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readText

typealias InputScope = PuzzleInput

private fun findDirectoryInAncestors(name: String) = generateSequence(Paths.get(name).toAbsolutePath()) {
    it.parent?.parent?.resolve(name)
}.firstOrNull { Files.isDirectory(it) }

private val searchPaths by lazy {
    listOfNotNull(
        findDirectoryInAncestors("inputs"),
        findDirectoryInAncestors("aoc-inputs")
    ).also { require(it.isNotEmpty()) { "Could not locate input directory" } }
}

fun interface InputScopeProvider {
    fun forPuzzle(year: Int, day: Int): PuzzleInput

    companion object : InputScopeProvider {
        val Example = InputScopeProvider { year, day ->
            PuzzleInput.of(resolveInputFile(year, day, "example.txt").readText())
        }
        override fun forPuzzle(year: Int, day: Int): PuzzleInput =
            PuzzleInput.of(resolveInputFile(year, day, "input.txt").readText())

        private fun resolveInputFile(year: Int, day: Int, name: String): Path {
            val path = "year-%d/day-%02d/%s".format(year, day, name)
            return searchPaths.firstNotNullOfOrNull { it.resolve(path).takeIf(Files::isReadable) }
                ?: error("No input $name for $year-$day in any of $searchPaths")
        }
    }
}
