package utils

fun <T> Iterable<T>.permute() = sequence<List<T>> {
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