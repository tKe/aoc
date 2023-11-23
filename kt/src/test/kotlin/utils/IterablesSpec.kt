package utils

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

class IterablesSpec : FreeSpec({
    fun <T> Sequence<List<T>>.repr() = map { it.joinToString("") }.toSet()

    "permute" - {
        // permutations(range(3)) --> 012 021 102 120 201 210
        "simple" {
            (0..2).permute().repr() shouldContainExactlyInAnyOrder "012 021 102 120 201 210".split(' ')
        }
    }

    "combinations" - {
        "python examples" - {
            "combinations('ABCD', 2) --> AB AC AD BC BD CD" {
                "ABCD".asIterable().combinations(2).repr() shouldContainExactlyInAnyOrder
                        "AB AC AD BC BD CD".split(' ')
            }
            "combinations(range(4), 3) --> 012 013 023 123" {
                (0..<4).combinations(3).repr() shouldContainExactlyInAnyOrder
                        "012 013 023 123".split(' ')
            }
        }
    }

    "combinationsWithReplacement" - {
        "python examples" - {
            "combinations_with_replacement('ABC', 2) --> AA AB AC BB BC CC" {
                "ABC".asIterable().combinations(2, replacement = true).repr() shouldContainExactlyInAnyOrder
                        "AA AB AC BB BC CC".split(' ')
            }
        }
    }
})
