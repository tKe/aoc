package aok

import kotlin.reflect.KProperty

@DslMarker
annotation class SolutionDsl


fun interface Parser<T> {
    fun PuzzleInput.parse(): T

    fun <R> map(f: (T) -> R) = Parser { f(parse()) }
    fun <R> andThen(f: (T) -> R) = map(f)
    context(input: PuzzleInput)
    operator fun invoke() = input.parse()

    context(input: PuzzleInput)
    operator fun <R> invoke(block: (T) -> R) = block(input.parse())

    operator fun invoke(input: String) = with(PuzzleInput.of(input)) { parse() }
}

fun <R> LineParser(mapper: (line: String) -> R) = Parser { lines.map(mapper) }
fun <K, V> MapParser(mapper: MutableMap<K, V>.(line: String) -> Unit) =
    Parser { buildMap(lines.size) { lines.forEach { mapper(it) } } }

fun <T, R> Parser<List<T>>.map(mapper: (T) -> R) = map { it.map(mapper) }

context(input: PuzzleInput)
private inline val puzzleInput
    get() = input

fun <T> Parser<T>.cached() = mutableMapOf<PuzzleInput, T>().run {
    Parser { getOrPut(puzzleInput) { this@cached() } }
}

@SolutionDsl
interface SolutionsScope<P1, P2> {
    fun <R> parser(block: Parser<R>) = block
    fun <R> lineParser(mapper: (line: String) -> R) = LineParser(mapper)

    @SolutionDsl
    fun part1(solution: Solution<P1>)

    @SolutionDsl
    fun part2(solution: Solution<P2>)

    @SolutionDsl
    fun <R> part1(parser: Parser<R>, solution: suspend (R) -> P1) = part1 { solution(parser()) }

    @SolutionDsl
    fun <R> part2(parser: Parser<R>, solution: suspend (R) -> P2) = part2 { solution(parser()) }
}

@SolutionDsl
fun interface PuzzleDefinition<P1, P2> {
    fun SolutionsScope<P1, P2>.build()
}

@SolutionDsl
fun interface Solution<T> {

    @SolutionDsl
    suspend fun PuzzleInput.solve(): T
}

abstract class Solutions<P1, P2> internal constructor(body: SolutionsScope<P1, P2>.(self: Any) -> Unit) {
    private val built by lazy {
        var part1 = Solution<P1> { TODO() }
        var part2 = Solution<P2> { TODO() }
        body(object : SolutionsScope<P1, P2> {
            override fun part1(solution: Solution<P1>) {
                part1 = solution
            }

            override fun part2(solution: Solution<P2>) {
                part2 = solution
            }
        }, this)
        part1 to part2
    }

    context(input: PuzzleInput)
    suspend fun part1() = with(built.first) { input.solve() }

    context(input: PuzzleInput)
    suspend fun part2() = with(built.second) { input.solve() }
}

abstract class PuzDSL(body: PuzzleDefinition<Any?, Any?>) : Solutions<Any?, Any?>(scope@{ with(body) { this@scope.build() } })
