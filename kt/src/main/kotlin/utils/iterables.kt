package utils

fun <T> Iterable<T>.permute() = sequence {
    val list = toMutableList()
    fun swap(i: Int, j: Int) = list[i].let {
        list[i] = list[j]
        list[j] = it
    }

    val c = IntArray(list.size)
    yield(list)
    var i = 1
    while (i < list.size) if (c[i] < i) {
        if (i % 2 == 0) swap(0, i) else swap(c[i], i)
        yield(list)
        c[i]++
        i = 1
    } else c[i++] = 0
}

fun <T> Iterable<T>.combinations(r: Int, replacement: Boolean = false) =
    if (replacement) combinationsWithReplacement(r) else combinations(r)

private fun <T> Iterable<T>.combinations(r: Int) = sequence {
    val pool = this@combinations as? List<T> ?: toList()
    if (r > pool.size) return@sequence
    val indices = IntArray(r) { it }

    yield(indices.map(pool::get))
    while (true) {
        val i = indices.indices.lastOrNull { indices[it] != it + pool.size - r } ?: break
        indices[i]++
        for (j in (i + 1)..<r) indices[j] = indices[j - 1] + 1
        yield(indices.map(pool::get))
    }
}

private fun <T> Iterable<T>.combinationsWithReplacement(r: Int) = sequence {
    val pool = this@combinationsWithReplacement as? List<T> ?: toList()
    if (pool.isEmpty() || r == 0) return@sequence
    val indices = IntArray(r)

    yield(indices.map(pool::get))
    while (true) {
        val i = indices.indices.lastOrNull { indices[it] != pool.lastIndex } ?: break
        indices[i..indices.lastIndex] = indices[i] + 1
        yield(indices.map(pool::get))
    }
}

fun <T> Iterable<T>.splitWhen(predicate: (T) -> Boolean) = sequence {
    val buf = mutableListOf<T>()
    for (value in this@splitWhen) {
        if (predicate(value)) {
            if (buf.isNotEmpty()) yield(buf.toList())
            buf.clear()
        } else buf.add(value)
    }
}

inline fun Iterable<String>.forEachCharIndexed(block: (x: Int, y: Int, c: Char) -> Unit) =
    forEachIndexed { y, n -> n.forEachIndexed { x, c -> block(x, y, c) } }

@OverloadResolutionByLambdaReturnType
@OptIn(kotlin.experimental.ExperimentalTypeInference::class)
inline fun List<String>.sumOfEachCharIndexed(block: (x: Int, y: Int, c: Char) -> Int): Int {
    var sum = 0
    forEachCharIndexed { x, y, c -> sum += block(x, y, c) }
    return sum
}

@OverloadResolutionByLambdaReturnType
@OptIn(kotlin.experimental.ExperimentalTypeInference::class)
inline fun List<String>.sumOfEachCharIndexed(block: (x: Int, y: Int, c: Char) -> Long): Long {
    var sum = 0L
    forEachCharIndexed { x, y, c -> sum += block(x, y, c) }
    return sum
}

private operator fun IntArray.set(indices: IntRange, value: Int) = indices.forEach { set(it, value) }