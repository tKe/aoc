package utils

import aok.LineParser
import aok.Parser
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun Sequence<String>.mapIntsNotNull() = mapNotNull(String::toIntOrNull)
fun Iterable<String>.mapIntsNotNull() = mapNotNull(String::toIntOrNull)
fun String.splitIntsNotNull(vararg delimiters: String = arrayOf(" ")) =
    split(*delimiters).mapIntsNotNull()

fun String.splitInts(vararg delimiters: Char = charArrayOf(' ')) =
    split(*delimiters).map(String::toInt)

fun String.splitLongs(vararg delimiters: Char = charArrayOf(' ')) =
    split(*delimiters).map(String::toLong)

fun String.splitLongsNotNull(vararg delimiters: Char = charArrayOf(' ')) =
    split(*delimiters).mapNotNull(String::toLongOrNull)

fun String.splitOnce(delimiter: String): Pair<String, String> {
    splitOnce(delimiter) { a, b -> return a to b }
}

@OptIn(ExperimentalContracts::class)
inline fun String.splitOnce(delimiter: String, f: (String, String) -> Unit) {
    contract {
        callsInPlace(f, InvocationKind.EXACTLY_ONCE)
    }
    f(substringBefore(delimiter), substringAfter(delimiter))
}

fun <R> String.splitOnce(delimiter: String, transform: (String) -> R) =
    transform(substringBefore(delimiter)) to transform(substringAfter(delimiter))

fun <L, R> String.splitOnce(delimiter: String, transformLeft: (String) -> L, transformRight: (String) -> R) =
    transformLeft(substringBefore(delimiter)) to transformRight(substringAfter(delimiter))

fun String.replace(vararg replacements: Pair<String, String>) =
    replacements.fold(this) { s, (a, b) -> s.replace(a, b) }

object Parsers {
    object Ints : Parser<List<Int>> by (LineParser(String::toIntOrNull).map { it.filterNotNull() })
    object Longs : Parser<List<Long>> by (Parser { input.splitLongs(' ', '\n') })
}