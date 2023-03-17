package aok

import kotlin.reflect.KProperty

@DslMarker
annotation class SolutionDsl


@SolutionDsl
interface SolutionsScope<P1, P2> {
    fun <R> parser(block: PuzzleInput.() -> R) = block
    fun <R> lineParser(mapper: (String) -> R) = parser { lines.map(mapper) }
    @SolutionDsl
    fun part1(solution: Solution<P1>)

    @SolutionDsl
    fun part2(solution: Solution<P2>)
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
