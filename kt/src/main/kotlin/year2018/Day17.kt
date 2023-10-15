package year2018

import aok.PuzDSL
import aoksp.AoKSolution
import year2018.Day17.flow

fun main(): Unit = solveDay(
    17,
    warmup = aok.Warmup.iterations(3), runs = 5,
//    input = aok.InputProvider.raw(
//        """
//            x=495, y=2..7
//            y=7, x=495..501
//            x=501, y=3..7
//            x=498, y=2..4
//            x=506, y=1..2
//            x=498, y=10..13
//            x=504, y=10..13
//            y=13, x=498..504
//        """.trimIndent()
//    )
)

@AoKSolution
object Day17 : PuzDSL({
    val parse = parser {
        val (hori, vert) = lines.partition { it[0] == 'y' }
        fun List<String>.rangeLookup() = map { it.split(',', '=', '.').mapNotNull(String::toIntOrNull) }
            .groupBy({ (at) -> at }) { (_, start, end) -> start..end }
        ClayLocator(hori.rangeLookup(), vert.rangeLookup())
    }

    part1(parse) { clay ->
        with(clay) {
            val (static, flowing) = flow()
            (static + flowing).count { it.y in yRange }
        }
    }

    part2(parse) { clay ->
        with(clay) {
            val (static) = flow()
            static.count { it.y in yRange }
        }
    }
}) {
    private fun Iterable<IntRange>.combine() = minOf { it.first }..maxOf { it.last }
    private fun IntRange.expandFor(others: Iterable<Int>) = others.fold(this) { r, i ->
        if (i in r) r
        else {
            println("expanding $r to include $i")
            if (i < r.first) i..r.last
            else r.first..i
        }
    }

    context(ClayLocator) fun debug(f: (Int2) -> String? = { null }) {
        for (y in yRange) {
            for (x in xRange) print(f(Int2(x, y)) ?: if (isClay(x, y)) "🪨" else "🥪")
            println()
        }
        println()
    }

    class ClayLocator(private val hori: Map<Int, List<IntRange>>, private val vert: Map<Int, List<IntRange>>) {
        val xRange = hori.values.flatten().combine().expandFor(vert.keys)
        val yRange = vert.values.flatten().combine().expandFor(hori.keys)
        fun isClay(x: Int, y: Int) = vert[x].orEmpty().any { y in it } || hori[y].orEmpty().any { x in it }
    }

    data class Int2(val x: Int, val y: Int) {
        val down get() = copy(y = y + 1)
        val left get() = copy(x = x - 1)
        val right get() = copy(x = x + 1)
    }

    context(ClayLocator)
    val Int2.isClay
        get() = isClay(x, y)


    context(ClayLocator)
    private fun Set<Int2>.debug(vararg sources: Int2) {
        debug {
            when (it) {
                in sources -> "🌊"
                in this -> "💧"
                else -> null
            }
        }
    }

    context(ClayLocator)
    fun flow(from: Int2 = Int2(500, 0)): Pair<MutableSet<Int2>, MutableSet<Int2>> {
        val falling = sortedSetOf(compareByDescending(Int2::y).thenBy(Int2::x), from)
        val flowing = mutableSetOf<Int2>()
        val static = mutableSetOf<Int2>()

        fun Int2?.d() = debug {
            when (it) {
                this -> "💦"
                in falling -> "🔽"
                in static -> "💧"
                in flowing -> "↔️"
                else -> null
            }
        }.also { readln() }

        fun Int2.hasSupport() = down.let { it.isClay || it in static }

        fun Int2.flowIn() = buildSet {
            fun add(dir: Int2.() ->Int2) {
                var loc = dir()
                while (!loc.isClay) {
                    add(loc)
                    if (!loc.hasSupport()) break
                    loc = loc.dir()
                }
            }
            add(this@flowIn)
            add(Int2::left)
            add(Int2::right)
        }

        while (falling.isNotEmpty()) {
            val flow = falling.pollFirst() ?: break
            if (flow.down in flowing || flow.y == yRange.last) {
                falling -= flow
                flowing += flow
            } else if (flow.hasSupport()) {
                val flowed = flow.flowIn()
                val falls = flowed.filterNot(Int2::hasSupport)
                if (falls.isEmpty()) {
                    static += flowed
                    flowing -= flowed
                } else {
                    flowing += flowed
                    falling += falls
                }
            } else {
                falling += flow // add us back
                falling += flow.down
            }
        }

        return static to flowing
    }
}

