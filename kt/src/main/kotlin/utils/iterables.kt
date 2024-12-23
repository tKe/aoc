package utils

import java.util.*

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

fun <T> Iterable<T>.combinations(r: Int, replacement: Boolean = false) = sequence {
    val pool = this@combinations as? List<T> ?: toList()
    indexCombinations(pool.size, r, replacement) { indices ->
        yield(indices.map(pool::get))
    }
}

inline fun indexCombinations(size: Int, take: Int, replacement: Boolean = false, visit: (IntArray) -> Unit) = when {
    take > size -> Unit
    replacement -> combinationsWithReplacement(size, take, visit)
    else -> combinations(size, take, visit)
}

@PublishedApi
internal inline fun combinations(size: Int, take: Int, visit: (IntArray) -> Unit) {
    val indices = IntArray(take) { it }

    visit(indices)
    while (true) {
        val i = indices.indices.lastOrNull { indices[it] != it + size - take } ?: break
        indices[i]++
        for (j in (i + 1)..<take) indices[j] = indices[j - 1] + 1
        visit(indices)
    }
}

@PublishedApi
internal inline fun combinationsWithReplacement(size: Int, take: Int, visit: (IntArray) -> Unit) {
    val indices = IntArray(take) { it }
    val lastIndex = size - 1

    visit(indices)
    while (true) {
        val i = indices.indices.lastOrNull { indices[it] != lastIndex } ?: break
        indices[i..indices.lastIndex] = indices[i] + 1
        visit(indices)
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

@PublishedApi
internal operator fun IntArray.set(indices: IntRange, value: Int) =
    Arrays.fill(this, indices.first, indices.last + 1, value)