package aok

import java.net.CookieManager
import java.net.HttpCookie
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodySubscribers
import java.nio.charset.Charset
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
        fun of(file: Path): PuzzleInput = of(file.readText().trimEnd())
        fun of(year: Int, day: Int, fileName: String): PuzzleInput =
            findInputFile(year, day, fileName)?.let(::of)
                ?: error("No input '$fileName' for $year-$day in any of $searchPaths")
    }

    private data class Impl(override val input: String) : PuzzleInput {
        override val lines = input.lines()
        override val lineSeq = lines.asSequence()
        override fun toString() = "\"${input.replace("\"", "\\\"")}\""
    }
}


private fun findDirectoryInAncestors(name: String) = generateSequence(Paths.get(name).toAbsolutePath()) {
    it.parent?.parent?.resolve(name)
}.firstOrNull { Files.isDirectory(it) }

private fun findInputFile(path: String) =
    searchPaths.firstNotNullOfOrNull { it.resolve(path).takeIf(Files::isReadable) }

private fun findInputFile(year: Int, day: Int, name: String = "input.txt") =
    findInputFile(inputFilePath(year, day, name))

private fun inputFilePath(year: Int, day: Int, name: String = "input.txt") =
    "year-%d/day-%02d/%s".format(year, day, name)

private val searchPaths by lazy {
    listOfNotNull(
        findDirectoryInAncestors("inputs"),
        findDirectoryInAncestors("aoc-inputs")
    ).also { require(it.isNotEmpty()) { "Could not locate input directory" } }
}

@JvmInline
value class FixedInput(private val input: PuzzleInput) : InputProvider {
    override fun forPuzzle(year: Int, day: Int) = input
    override fun toString() = "Fixed($input)"
}


private fun fileInputProvider(file: String, name: String = file.removeSuffix(".txt")) =
    InputProvider(name) { year, day -> PuzzleInput.of(year, day, file) }

fun InputProvider(name: String, provider: InputProvider) = object : InputProvider by provider {
    override fun toString() = name
}


fun interface InputProvider {
    fun forPuzzle(year: Int, day: Int): PuzzleInput

    companion object : InputProvider {
        val Example = fileInputProvider("example.txt")
        val Default = this
        override fun toString() = "default"
        override fun forPuzzle(year: Int, day: Int) =
            PuzzleInput.of(findInputFile(year, day) ?: downloadInput(year, day))

        fun raw(content: String) = FixedInput(PuzzleInput.of(content))
        fun forFile(name: String) = fileInputProvider(name)
    }
}

fun <R> withInput(content: String, block: context(PuzzleInput) () -> R) = PuzzleInput.of(content).run(block)


private fun downloadInput(year: Int, day: Int): Path {
    val sessionFile = findInputFile(".session") ?: error("unable to download input, no session file found")
    val dest = sessionFile.resolveSibling(inputFilePath(year, day))
    val sessionCookie = sessionFile.readText().trim()

    val source = "https://adventofcode.com/$year/day/$day/input".let(URI::create)

    println("WARN: Downloading $source to $dest")

    val req = HttpRequest.newBuilder().GET().uri(source).build()
    val res = HttpClient.newBuilder()
        .cookieHandler(CookieManager().apply {
            cookieStore.add(
                URI.create("https://adventofcode.com"),
                HttpCookie("session", sessionCookie).apply {
                    version = 0
                    path = "/"
                    isHttpOnly = true
                    secure = true
                })
        })
        .build()
        .send(req) {
            if (it.statusCode() == 200) BodySubscribers.ofFile(dest)
            else BodySubscribers.ofString(Charset.defaultCharset())
        }

    return when (val body = res.body()) {
        is Path -> body
        else -> error(body)
    }
}