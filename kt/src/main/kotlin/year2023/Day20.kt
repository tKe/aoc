package year2023

import aok.MapParser
import aok.Parser
import aok.PuzDSL
import aoksp.AoKSolution
import utils.lcm

fun main() = solveDay(20)

@AoKSolution
object Day20 : PuzDSL({
    part1(ModMap) { moduleMap ->
        var (highs, lows) = 0 to 0
        repeat(1000) {
            moduleMap.pushButton { (_, n, high) -> if (high) highs += n.size else lows += n.size }
        }
        highs * lows
    }

    part2(ModMap) { modules ->
        val greatConjunction = (modules.transmitter as? Conjunction)?.inputs
            ?: error("transmitter is not a conjunction")
        buildMap {
            var pushes = 0L
            while (size < greatConjunction.size) {
                modules.pushButton { (from, _, high) ->
                    if (from == "button") pushes++
                    if (high && from in greatConjunction && from !in this) this[from] = pushes
                }
            }
        }.values.reduce(::lcm)
    }
}) {
    data class Pulse(val source: String, val destinations: List<String>, val high: Boolean)

    @JvmInline
    value class ModMap(private val modules: Map<String, Module>) {
        init {
            // seed conjunction inputs
            modules.onEach { (name, module) ->
                module.destinations.mapNotNull(modules::get)
                    .filterIsInstance<Conjunction>()
                    .forEach { it.receive(name, false) }
            }
        }

        val transmitter get() = modules.values.single { "rx" in it.destinations }

        fun pushButton(pulse: (Pulse) -> Unit) {
            val pending = ArrayDeque(listOf(Pulse("button", listOf("broadcaster"), false)))
            while (pending.isNotEmpty()) {
                val (source, destinations, high) = pending.removeFirst().also(pulse)
                for (destination in destinations) modules[destination]?.let {
                    it.receive(source, high)?.let { pulse ->
                        pending += Pulse(destination, it.destinations, pulse)
                    }
                }
            }
        }

        companion object : Parser<ModMap> by (MapParser {
            val (module, outbound) = it.split(" -> ")
            val destinations = outbound.split(", ")
            when (module.first()) {
                '%' -> put(module.drop(1), FlipFlop(destinations))
                '&' -> put(module.drop(1), Conjunction(destinations))
                else -> put(module, Broadcaster(destinations))
            }
        }.map(::ModMap))
    }

    sealed class Module(val destinations: List<String>) {
        abstract fun receive(from: String, high: Boolean): Boolean?
    }

    class Broadcaster(destinations: List<String>) : Module(destinations) {
        override fun receive(from: String, high: Boolean) = high
    }

    class FlipFlop(destinations: List<String>) : Module(destinations) {
        private var state = false
        override fun receive(from: String, high: Boolean) =
            if (!high) state.not().also { state = it } else null
    }

    class Conjunction(destinations: List<String>) : Module(destinations) {
        private val states = mutableMapOf<String, Boolean>()
        val inputs: Set<String> get() = states.keys
        override fun receive(from: String, high: Boolean) =
            states.also { it[from] = high }.values.any { !it }
    }
}
