package year2015

import aok.PuzDSL
import aoksp.AoKSolution
import java.security.MessageDigest

@AoKSolution
object Day04 : PuzDSL({
    val md5 = MessageDigest.getInstance("MD5")
    operator fun MessageDigest.invoke(str: String) = md5.digest(str.encodeToByteArray())

    fun decode(key: String, predicate: (ByteArray) -> Boolean) =
        (1..Int.MAX_VALUE).first { predicate(md5("$key$it")) }

    part1 { decode(input.trim()) { (a, b, c) -> a == 0.toByte() && b == 0.toByte() && c in 0..0xF } }
    part2 { decode(input.trim()) { (a, b, c) -> a == 0.toByte() && b == 0.toByte() && c == 0.toByte() } }
})

fun main() = solveDay(4)
