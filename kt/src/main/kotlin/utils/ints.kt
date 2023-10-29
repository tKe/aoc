package utils

tailrec fun gcd(a: Int, b: Int): Int = when {
    a == 0 -> b
    b == 0 -> a
    else -> gcd(minOf(a, b), maxOf(a, b) % minOf(a, b))
}

tailrec fun gcd(a: Long, b: Long): Long = when {
    a == 0L -> b
    b == 0L -> a
    else -> gcd(minOf(a, b), maxOf(a, b) % minOf(a, b))
}

fun lcm(a: Long, b: Long): Long = (a * b) / gcd(a, b)
