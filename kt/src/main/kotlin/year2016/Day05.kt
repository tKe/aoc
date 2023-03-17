package year2016

import aok.InputProvider
import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution
import java.security.MessageDigest
import kotlin.experimental.and

@AoKSolution
object Day05 : PuzDSL({
    val md5 = MessageDigest.getInstance("MD5")
    operator fun MessageDigest.invoke(str: String) = md5.digest(str.encodeToByteArray())

    fun decode(key: String) = generateSequence(0, Int::inc)
        .map { md5("$key$it").take(3) }
        .filter { (a, b, c) -> a == 0.toByte() && b == 0.toByte() && c.toUByte() < 16u }
        .take(8)
        .joinToString("") { (_, _, c) -> c.toString(16) }

    part1 {
        decode(input.trim())
    }

    fun decodeAdvanced(key: String) = generateSequence(0, Int::inc)
        .map { md5("$key$it").take(4) }
        .filter { (a, b, c) -> a == 0.toByte() && b == 0.toByte() && c.toUByte() < 8u }
        .map { (_, _, pos, char) ->
            pos.toInt() to char.toUByte().toInt().shr(4).digitToChar(16).lowercaseChar()
        }
        .runningFold("--------".toCharArray()) { pass, (pos, digit) ->
            if (pass[pos] == '-') pass[pos] = digit
            pass
        }
        .onEach(::println)
        .first { it.none { c -> c == '-' } }
        .concatToString()

    part2 {
        decodeAdvanced(input.trim())
    }
})


fun main() = solveDay(
    5,
    input = InputProvider.raw("ugkcyxxp")
//    input = InputProvider.raw("abc")
)
