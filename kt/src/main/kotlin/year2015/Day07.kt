package year2015

import aok.PuzDSL
import aok.Warmup
import aoksp.AoKSolution
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day07 : PuzDSL({
    suspend fun <R> circuit(
        routes: List<String>,
        block: suspend (wire: suspend (String) -> MutableSharedFlow<UShort>) -> R
    ) = coroutineScope {
        val wire = with(mutableMapOf<String, MutableSharedFlow<UShort>>()) {
            { wire: String -> getOrPut(wire) { MutableSharedFlow(1) } }
        }

        infix fun UShort.shl(other: UShort) = (toInt() shl other.toInt()).toUShort()
        infix fun UShort.shr(other: UShort) = (toInt() ushr other.toInt()).toUShort()

        fun gate(expr: String): Flow<UShort> {
            fun read(operand: String) = operand.toUShortOrNull()?.let(::flowOf) ?: wire(operand)
            val it = expr.split(" ")
            return when (it.size) {
                1 -> read(it.single())
                2 -> read(it[1]).map(UShort::inv)
                else -> {
                    val operation = when (it[1]) {
                        "LSHIFT" -> UShort::shl
                        "RSHIFT" -> UShort::shr
                        "AND" -> UShort::and
                        "OR" -> UShort::or
                        else -> error("unhandled op ${it[1]}")
                    }
                    read(it[0]).combine(read(it[2]), operation)
                }
            }
        }

        val circuit = launch(Dispatchers.Unconfined) {
            for ((expr, target) in routes.map { it.split(" -> ") })
                launch { gate(expr).collect(wire(target)) }
        }

        block(wire).also { circuit.cancel() }
    }

    part1 { circuit(lines) { wire -> wire("a").first() } }

    part2 {
        circuit(lines) { wire ->
            wire("a").take(1).collect(wire("b"))
            wire("a").first()
        }
    }
})

fun main() = solveDay(7, warmup = Warmup.eachFor(5.seconds))
