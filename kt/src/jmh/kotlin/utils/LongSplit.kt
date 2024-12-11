package utils

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.infra.Blackhole
import kotlin.math.absoluteValue
import kotlin.random.Random

open class LongSplit {
    @State(Scope.Thread)
    open class Values {
        val longs = LongArray(1000) { Random.nextLong(0L, Long.MAX_VALUE / 2) }
    }

    @Benchmark
    fun strings(blackhole: Blackhole, values: Values) =
        Pow10String.run { values.longs.forEach { blackhole.consume(it.split) } }

    @Benchmark
    fun lookup(blackhole: Blackhole, values: Values) =
        Pow10.run { values.longs.forEach { blackhole.consume(it.split) } }

    @Benchmark
    fun binarySearch(blackhole: Blackhole, values: Values) =
        Pow10Binary.run { values.longs.forEach { blackhole.consume(it.split) } }

    @Benchmark
    fun manual(blackhole: Blackhole, values: Values) =
        Manual.run { values.longs.forEach { blackhole.consume(it.split) } }

    @Benchmark
    fun whenLookup(blackhole: Blackhole, values: Values) =
        Pow10When.run { values.longs.forEach { blackhole.consume(it.split) } }

}

object Manual {
    val Long.split: Pair<Long, Long>?
        get() {
            var div = 10L
            var p10 = 1
            while (div <= this) {
                div *= 10
                p10++
            }
            if (p10 % 2 != 0) return null
            repeat(p10 / 2) { div /= 10 }
            return this / div to this % div
        }
}

object Pow10 {
    private val longPowersOf10 = LongArray(19) { 1L }
        .apply { for (i in 1..lastIndex) for (j in i..lastIndex) this[j] *= 10L }

    val Long.split: Pair<Long, Long>?
        get() = longPowersOf10.indexOfFirst { it > this }.takeIf { it % 2 == 0 }
            ?.let { idx ->
                val pow = longPowersOf10[idx / 2]
                this / pow to this % pow
            }
}

object Pow10When {
    val Long.split: Pair<Long, Long>? get() = when(this) {
        in 10L..99L -> this / 10 to this % 10
        in 1000L..9999L -> this / 100 to this % 100
        in 100000L..999999L -> this / 1000 to this % 1000
        in 10000000L..99999999L -> this / 10000 to this % 10000
        in 1000000000L..9999999999L -> this / 100000 to this % 100000
        in 100000000000L..999999999999L -> this / 1000000 to this % 1000000
        in 10000000000000L..99999999999999L -> this / 10000000 to this % 10000000
        in 1000000000000000L..9999999999999999L -> this / 100000000 to this % 100000000
        in 100000000000000000L..999999999999999999L -> this / 1000000000 to this % 1000000000
        else -> null
    }
}

object Pow10String {
    val Long.split: Pair<Long, Long>?
        get() = toString().let {
            if (it.length % 2 == 0) it.substring(0, it.length / 2).toLong() to it.substring(it.length / 2).toLong()
            else null
        }
}

object Pow10Binary {
    private val pow10s = generateSequence(1L) { it * 10 }.takeWhile { it >= 0L }.toList().toLongArray()
    private val longPows = pow10s.copyOf().apply { indices.forEach { this[it]-- } }
    val Long.digits: Int get() = longPows.binarySearch(this).let { if (it < 0) it.inc().absoluteValue else it }
    val Long.split: Pair<Long, Long>?
        get() = digits.let { if (it % 2 == 0) pow10s[it / 2].let { this / it to this % it } else null }
}