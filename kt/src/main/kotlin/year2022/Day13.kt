package year2022

import aok.InputProvider
import aok.PuzzleInput
import aok.input
import aoksp.AoKSolution
import arrow.core.compareTo
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.DecodeSequenceMode.WHITESPACE_SEPARATED
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeToSequence
import aok.solveAll
import aok.warmup

fun main(): Unit = with(InputProvider) {
    queryDay(13).warmup(
        iterations = 300
    ).solveAll(
        runIterations = 3
    )
}

@AoKSolution
object Day13 {
    context (_: PuzzleInput)
    fun part1() = packets()
        .mapIndexedNotNull { idx, (a, b) -> (idx + 1).takeIf { a <= b } }
        .sum()

    context (_: PuzzleInput)
    fun part2(): Int {
        val dividers = setOf(
            PacketList(PacketList(PacketInt(2))),
            PacketList(PacketList(PacketInt(6))),
        )
        return (packets().flatten() + dividers).sorted()
            .mapIndexedNotNull { idx, pkt -> (idx + 1).takeIf { pkt in dividers } }
            .reduce(Int::times)
    }

    sealed interface PacketEntry : Comparable<PacketEntry>
    data class PacketList(val values: List<PacketEntry>) : PacketEntry {
        constructor(vararg values: PacketEntry) : this(values.toList())

        override fun toString() = values.toString()
        override fun compareTo(other: PacketEntry) =
            values.compareTo(if (other is PacketList) other.values else listOf(other))
    }

    data class PacketInt(val value: Int) : PacketEntry {
        override fun toString() = value.toString()
        override fun compareTo(other: PacketEntry) = when (other) {
            is PacketInt -> value.compareTo(other.value)
            is PacketList -> listOf(this).compareTo(other.values)
        }
    }

    context(_: PuzzleInput)
    private fun packets() = input.trimEnd().split("\n\n")
        .map { chunk -> chunk.split("\n").map { it.parsePacketList() } }

    private fun String.parsePacketList(): PacketList = iterator().run {
        val listStack = ArrayDeque<MutableList<PacketEntry>>()
        var curInt = -1
        fun storeInt() = curInt.also {
            curInt = -1
            if (it >= 0) listStack[0] += PacketInt(it)
        }
        while (hasNext()) {
            when (val c = next()) {
                ',' -> storeInt()
                '[' -> listStack.addFirst(mutableListOf())
                ']' -> {
                    storeInt()
                    val list = PacketList(listStack.removeFirst())
                    if (listStack.isEmpty()) {
                        require(!hasNext()) { "unexpected end of input" }
                        return list
                    } else {
                        listStack[0] += list
                    }
                }

                else -> curInt = maxOf(curInt, 0) * 10 + c.digitToInt()
            }
        }
        error("didn't read end of list")
    }
}


@AoKSolution
object Day13Json {
    context (_: PuzzleInput)
    fun part1() = packets().chunked(2) { (a, b) -> a <= b }
        .withIndex().sumOf { (idx, it) -> if(it) idx + 1 else 0 }

    context (_: PuzzleInput)
    fun part2(): Int {
        val a = EL(EL(EI(2)))
        val b = EL(EL(EI(6)))
        var ia = 1
        var ib = 2
        val packets = packets()
        for (it in packets) {
            if(it < a) ia++
            if(it < b) ib++
        }
        return ia * ib
    }

    context(_: PuzzleInput)
    @OptIn(ExperimentalSerializationApi::class)
    private fun packets() = Json.decodeToSequence(input.byteInputStream(), ELSerializer, WHITESPACE_SEPARATED)

    sealed interface E : Comparable<E>
    data class EL(val values: List<E>) : E {
        constructor(vararg values: E) : this(values.toList())
        override fun compareTo(other: E) =
            values.compareTo(if (other is EL) other.values else listOf(other))
    }
    data class EI(val value: Int) : E {
        override fun compareTo(other: E) = when (other) {
            is EI -> value.compareTo(other.value)
            is EL -> listOf(this).compareTo(other.values)
        }
    }

    private fun <A, B> KSerializer<A>.map(m: (A) -> B) = object : KSerializer<B> {
        override val descriptor = this@map.descriptor
        override fun deserialize(decoder: Decoder): B = this@map.deserialize(decoder).let(m)
        override fun serialize(encoder: Encoder, value: B) = TODO()
    }
    val ELSerializer = ListSerializer(ESerializer).map(::EL)
    val EISerializer = Int.serializer().map(::EI)
    object ESerializer : JsonContentPolymorphicSerializer<E>(E::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out E> =
            if(element is JsonPrimitive) EISerializer else ELSerializer
    }
}
