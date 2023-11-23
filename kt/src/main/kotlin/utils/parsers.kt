package utils

fun Sequence<String>.mapIntsNotNull() = mapNotNull(String::toIntOrNull)
fun Iterable<String>.mapIntsNotNull() = mapNotNull(String::toIntOrNull)
fun String.splitIntsNotNull(vararg delimiters: String = arrayOf(" ")) =
    split(*delimiters).mapIntsNotNull()

fun String.replace(vararg replacements: Pair<String, String>) =
    replacements.fold(this) { s, (a, b) -> s.replace(a, b) }