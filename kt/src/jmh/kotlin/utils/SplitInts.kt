package utils

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import kotlin.random.Random

@State(Scope.Thread)
open class SplitInts {
    val raw = generateSequence { Random.nextInt(3000) }.take(13*51).toList().toIntArray()
    val data = raw.joinToString(",")

    @Benchmark
    fun mapToIntArray(): IntArray {
        fun String.splitInts(vararg delims: Char) = split(*delims).map(String::toInt).toIntArray()

        return data.splitInts(',')
    }

    @Benchmark
    fun recursive(): IntArray {
        fun String.splitInts(vararg delims: Char, offset: Int = 0, previous: Int = 0): IntArray {
            val next = indexOfAny(delims, offset)
            return if (next == -1) IntArray(previous + 1).also { it[it.lastIndex] = substring(offset).toInt() }
            else {
                splitInts(*delims, offset = next + 1, previous = previous + 1).also {
                    it[previous] = substring(offset, next).toInt()
                }
            }
        }
        return data.splitInts(',')
    }

    @Benchmark
    fun doubleScan(): IntArray {
        fun String.splitInts(vararg delims: Char): IntArray {
            val count = count { it in delims }
            val out = IntArray(count+1)
            var at = 0
            var idx = 0
            do {
                val next = indexOfAny(delims, at + 1)
                out[idx++] = (if(next == -1) substring(at) else substring(at, next)).toInt()
                at = next + 1
            } while(next != -1)
            return out
        }
        return data.splitInts(',')
    }
}

fun main() {
    val s = SplitInts()
    check(s.mapToIntArray().contentEquals(s.raw)) { "mapToIntArray failed" }
    s.recursive().let { check(it.contentEquals(s.raw)) { "recursive failed: \n${s.raw.contentToString()}\n${it.contentToString()}" } }
    check(s.doubleScan().contentEquals(s.raw)) { "doubleScan failed" }
}