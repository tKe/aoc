import kotlin.reflect.KProperty


@DslMarker
annotation class SolutionDsl

interface SolutionsScope<P1, P2> {
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
    fun InputScope.solve(): T
}

fun <P1, P2> PuzzleDefinition<P1, P2>.toSolutions() =
    with(object : SolutionsScope<P1, P2> {
        var part1 = Solution<P1> { TODO() }
        var part2 = Solution<P2> { TODO() }
        override fun part1(solution: Solution<P1>) {
            part1 = solution
        }

        override fun part2(solution: Solution<P2>) {
            part2 = solution
        }
    }) {
        build()
        part1 to part2
    }

operator fun <P1, P2> PuzzleDefinition<P1, P2>.provideDelegate(thisRef: Any, property: KProperty<*>) = lazy { toSolutions() }

context(InputScope)
operator fun <T> Solution<T>.invoke(): T = with(this) { solve() }
