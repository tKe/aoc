package year2016

import aok.InputProvider
import aok.PuzDSL
import aok.Warmup
import aoksp.AoKSolution
import java.security.MessageDigest
import java.util.HexFormat

@AoKSolution
object Day14 : PuzDSL({

    val hex = HexFormat.of()
    val md5 = MessageDigest.getInstance("MD5")
    fun ByteArray.hex(): String = hex.formatHex(this)
    fun String.md5() = md5.digest(toByteArray()).hex()

    fun md5Stream(salt: String, hasher: (String) -> String = String::md5) =
        generateSequence(0, Int::inc).map { hasher("$salt$it") }

    fun String.stretch() = generateSequence(this, String::md5).elementAt(2016)

    val matchThree = """(.)\1\1""".toRegex()
    fun Sequence<String>.keys() = windowed(1001, 1)
        .withIndex()
        .filter { (_, hashes) ->
            matchThree.find(hashes.first())?.let { m ->
                val (c) = m.destructured
                val confirmation = c.repeat(5)
                hashes.drop(1).any { confirmation in it }
            } == true
        }.map { IndexedValue(it.index, it.value.first()) }

    part1 {
        md5Stream(input.trim())
            .keys().elementAt(63).index
    }
    part2 {
        md5Stream(input.trim())
            .map(String::stretch)
            .keys().elementAt(63).index
    }
})

fun main() = solveDay(
    14,
)
