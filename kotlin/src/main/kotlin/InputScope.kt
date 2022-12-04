
import InputScope.Companion.remapped
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

    companion object : InputScopeProvider {
        override fun forPuzzle(year: Int, day: Int): InputScope = Simple { name ->
            val path = "year-%d/day-%02d/%s".format(year, day, name)
            searchPaths.firstNotNullOfOrNull { it.resolve(path).takeIf(Files::isReadable) }
                ?: error("No input $name for $year-$day in any of $searchPaths")
        }

        fun InputScope.remapped(map: Map<String, String>): InputScope = Simple(this::resolveInput.compose { (map[it] ?: it).also(::println) })
        fun InputScope.remapped(vararg pairs: Pair<String, String>) = remapped(pairs.toMap())
    }

    private class Simple(private val resolver: (String) -> Path) : InputScope {
        override fun resolveInput(name: String) = resolver(name)
        override val input = readInput()
        override val lines = input.trim().lines()
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

fun interface InputScopeProvider {
    fun forPuzzle(year: Int, day: Int): InputScope

    companion object : InputScopeProvider by InputScope {
        fun mapping(vararg pairs: Pair<String, String>) =
            InputScopeProvider { year, day -> forPuzzle(year, day).remapped(pairs.toMap()) }
    }
}
