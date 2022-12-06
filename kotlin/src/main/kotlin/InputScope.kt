import arrow.core.compose
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

sealed interface InputScope {
    val input: String
    val lines: List<String>
    val lineSeq: Sequence<String>

    fun resolveInput(name: String = "input.txt"): Path
    fun readInput(name: String = "input.txt"): String = readInput(name, Files::readString)
    fun <R> readInput(name: String = "input.txt", transform: (Path) -> R) = transform(resolveInput(name))
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

private class SimpleInputScope(private val resolver: (String) -> Path) : InputScope {
    override fun resolveInput(name: String) = resolver(name)
    override val input = readInput()
    override val lines = input.trim().lines()
    override val lineSeq = lines.asSequence()
}

fun interface InputScopeProvider {
    fun forPuzzle(year: Int, day: Int): InputScope

    companion object : InputScopeProvider {
        override fun forPuzzle(year: Int, day: Int): InputScope = SimpleInputScope { name ->
            val path = "year-%d/day-%02d/%s".format(year, day, name)
            searchPaths.firstNotNullOfOrNull { it.resolve(path).takeIf(Files::isReadable) }
                ?: error("No input $name for $year-$day in any of $searchPaths")
        }

        private fun InputScope.compose(map: (String) -> String): InputScope =
            SimpleInputScope(this::resolveInput.compose(map))

        fun mapping(vararg pairs: Pair<String, String>) =
            if (pairs.isEmpty()) InputScopeProvider
            else {
                val map = pairs.toMap()
                InputScopeProvider { year, day ->
                    forPuzzle(year, day).compose { map[it] ?: it }
                }
            }
    }
}
