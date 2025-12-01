package year2024

import aok.MapParser
import aok.Parser
import aok.PuzzleInput
import aok.checkAll
import aok.solveAll
import aoksp.AoKSolution
import utils.splitOnce

@AoKSolution
object Day24 {
    context(_: PuzzleInput) fun part1() = parse { (_, _, z) -> z }

    context(_: PuzzleInput) fun part2() = parse { circuit ->
        // check some rules of
        // half adder of (x, y, ci) -> (z, co)
        // (1) x XOR y -> r
        // (2) x AND y -> ca
        // (3) r XOR ci -> z
        // (4) r AND ci -> cb
        // (5) ca OR cb -> co
        // ignore special case for x00 ^ y00 -> z00 and z45 == last co

        val swaps = buildList {
            fun add(a: String, b: String) = if (a > b) add(b to a) else add(a to b)

            for (bit in 1..44) {
                val (x, y, z) = "xyz".map { "%s%02d".format(it, bit) }

                val r = circuit.get<Gate.Xor>(x, y)
                val ca = circuit.get<Gate.And>(x, y)
                val rOut = circuit.find<Gate.Xor>(r)
                    ?: circuit.find<Gate.Xor>(ca)?.also { add(ca, r) }
                    ?: error("wire from an `$x (XOR|AND) $y` gate not found")
                if (rOut != z) add(rOut, z)
            }

            check(size == 4) { "expected exactly 4 swaps but found $size" }
        }

        val (x, y, z) = circuit.swap(swaps)
        check(x + y == z) { "incorrect addition, expected $x + $y == ${x + y} but was $z" }
        swaps.flatMap { (a, b) -> listOf(a, b) }.sorted().joinToString(",")
    }

    private sealed interface Gate {
        data class Constant(val value: Boolean) : Gate
        sealed class Logic(op: (Boolean, Boolean) -> Boolean) : Gate, ((Boolean, Boolean) -> Boolean) by op {
            abstract val left: String
            abstract val right: String
            override fun toString() = "$left ${this::class.simpleName} $right"
            operator fun contains(wire: String) = wire == left || wire == right
        }

        data class Xor(override val left: String, override val right: String) : Logic(Boolean::xor)
        data class Or(override val left: String, override val right: String) : Logic(Boolean::or)
        data class And(override val left: String, override val right: String) : Logic(Boolean::and)
    }

    @JvmInline
    private value class Circuit(val nodes: Map<String, Gate>) : Map<String, Gate> by nodes {
        operator fun component1() = get('x')
        operator fun component2() = get('y')
        operator fun component3() = get('z')

        private fun <K, V> MutableMap<K, V>.swap(pair: Pair<K, K>) =
            put(pair.second, put(pair.first, getValue(pair.second))!!)

        fun swap(wirePairs: Iterable<Pair<String, String>>): Circuit {
            val swapped = nodes.toMutableMap()
            wirePairs.forEach { swapped.swap(it) }
            return Circuit(swapped)
        }

        inline fun <reified G : Gate.Logic> get(a: String, b: String): String =
            entries.single { (_, gate) -> gate is G && a in gate && b in gate }.key

        inline fun <reified G : Gate.Logic> find(a: String): String? =
            entries.find { (_, gate) -> gate is G && a in gate }?.key

        private fun read(wire: String): Boolean = when (val gate = getValue(wire)) {
            is Gate.Constant -> gate.value
            is Gate.Logic -> gate.invoke(read(gate.left), read(gate.right))
        }

        private operator fun get(reg: Char): Long =
            keys.filter { it[0] == reg }.sorted()
                .foldIndexed(0L) { idx, acc, wire ->
                    if (read(wire)) acc or (1L shl idx) else acc
                }
    }

    private val parse = Parser {
        val (inputs, connections) = input.splitOnce("\n\n")
        Circuit(buildMap {
            for (input in inputs.lines()) {
                input.splitOnce(": ") { wire, value ->
                    put(wire, Gate.Constant(value == "1"))
                }
            }
            for (connection in connections.lines()) {
                val (a, op, b, out) = connection.split(" -> ", " ")
                val left = minOf(a, b)
                val right = maxOf(a, b)
                when (op) {
                    "XOR" -> put(out, Gate.Xor(left, right))
                    "OR" -> put(out, Gate.Or(left, right))
                    "AND" -> put(out, Gate.And(left, right))
                }
            }
        })
    }
}

fun main(): Unit = queryDay(24).run {
    checkAll(part1 = 4) {
        """
        x00: 1
        x01: 1
        x02: 1
        y00: 0
        y01: 1
        y02: 0

        x00 AND y00 -> z00
        x01 XOR y01 -> z01
        x02 OR y02 -> z02
        """.trimIndent()
    }
    checkAll(part1 = 2024) {
        """
        x00: 1
        x01: 0
        x02: 1
        x03: 1
        x04: 0
        y00: 1
        y01: 1
        y02: 1
        y03: 1
        y04: 1
        
        ntg XOR fgs -> mjb
        y02 OR x01 -> tnw
        kwq OR kpj -> z05
        x00 OR x03 -> fst
        tgd XOR rvg -> z01
        vdt OR tnw -> bfw
        bfw AND frj -> z10
        ffh OR nrd -> bqk
        y00 AND y03 -> djm
        y03 OR y00 -> psh
        bqk OR frj -> z08
        tnw OR fst -> frj
        gnj AND tgd -> z11
        bfw XOR mjb -> z00
        x03 OR x00 -> vdt
        gnj AND wpb -> z02
        x04 AND y00 -> kjc
        djm OR pbm -> qhw
        nrd AND vdt -> hwm
        kjc AND fst -> rvg
        y04 OR y02 -> fgs
        y01 AND x02 -> pbm
        ntg OR kjc -> kwq
        psh XOR fgs -> tgd
        qhw XOR tgd -> z09
        pbm OR djm -> kpj
        x03 XOR y03 -> ffh
        x00 XOR y04 -> ntg
        bfw OR bqk -> z06
        nrd XOR fgs -> wpb
        frj XOR qhw -> z04
        bqk OR frj -> z07
        y03 OR x01 -> nrd
        hwm AND bqk -> z03
        tgd XOR rvg -> z12
        tnw OR pbm -> gnj
        """.trimIndent()
    }
    checkAll(part1 = 46463754151024, part2 = "cqk,fph,gds,jrs,wrk,z15,z21,z34")
    solveAll()
}
