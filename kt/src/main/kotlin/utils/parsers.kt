package utils

import aok.LineParser
import aok.Parser

fun Sequence<String>.mapIntsNotNull() = mapNotNull(String::toIntOrNull)
fun Iterable<String>.mapIntsNotNull() = mapNotNull(String::toIntOrNull)
fun String.splitIntsNotNull(vararg delimiters: String = arrayOf(" ")) =
    split(*delimiters).mapIntsNotNull()

fun String.replace(vararg replacements: Pair<String, String>) =
    replacements.fold(this) { s, (a, b) -> s.replace(a, b) }

object Parsers {
    object Ints : Parser<List<Int>> by (LineParser(String::toIntOrNull).map{ it.filterNotNull() })
}