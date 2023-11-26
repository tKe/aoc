package aok

import kotlin.reflect.KProperty

@DslMarker
annotation class SolutionDsl


fun interface Parser<T> {
    context(PuzzleInput)
    fun parse(): T
    fun <R> map(f: (T) -> R) = Parser { f(parse()) }
    fun <R> andThen(f: (T) -> R) = map(f)
    context(PuzzleInput)
    operator fun invoke() = parse()
    operator fun invoke(input: String) = with(PuzzleInput.of(input)) { parse() }
}

fun <R> LineParser(mapper: (line: String) -> R) = Parser { lines.map(mapper) }

fun <T, R> Parser<List<T>>.map(mapper: (T) -> R) = map { it.map(mapper) }

@SolutionDsl
interface SolutionsScope<P1, P2> {
    fun <R> parser(block: Parser<R>) = block
    fun <R> lineParser(mapper: (line: String) -> R) = LineParser(mapper)

    @SolutionDsl
    fun part1(solution: Solution<P1>)

    @SolutionDsl
    fun part2(solution: Solution<P2>)

    @SolutionDsl
    fun <R> part1(parser: Parser<R>, solution: suspend (R) -> P1) = part1 { solution(parser.parse()) }

    @SolutionDsl
    fun <R> part2(parser: Parser<R>, solution: suspend (R) -> P2) = part2 { solution(parser.parse()) }
}

@SolutionDsl
fun interface PuzzleDefinition<P1, P2> {
    context(SolutionsScope<P1, P2>) fun build()
}

@SolutionDsl
fun interface Solution<T> {

    @SolutionDsl
    suspend fun PuzzleInput.solve(): T
}

private operator fun <P1, P2> PuzzleDefinition<P1, P2>.provideDelegate(thisRef: Any, property: KProperty<*>) = lazy {
    var part1 = Solution<P1> { TODO() }
    var part2 = Solution<P2> { TODO() }
    with(object : SolutionsScope<P1, P2> {
        override fun part1(solution: Solution<P1>) {
            part1 = solution
        }

        override fun part2(solution: Solution<P2>) {
            part2 = solution
        }
    }) { this@provideDelegate.build() }
    part1 to part2
}

abstract class PuzDSL(body: PuzzleDefinition<*, *>) {
    private val solutions by body

    context(PuzzleInput)
    suspend fun part1() = with(solutions.first) { solve() }

    context(PuzzleInput)
    suspend fun part2() = with(solutions.second) { solve() }
}
