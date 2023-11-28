package year2020

import aok.results
import aok.shouldGenerate
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import year2020.Day14.expand

class Day14Spec : FreeSpec({
    with(queryDay(14)) {
        """
            mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X
            mem[8] = 11
            mem[7] = 101
            mem[8] = 0
        """.trimIndent() shouldGenerate results(part1 = 165L)

        """
            mask = 000000000000000000000000000000X1001X
            mem[42] = 100
            mask = 00000000000000000000000000000000X0XX
            mem[26] = 1
        """.trimIndent() shouldGenerate results(part2 = 208L)
    }

    "mask expansion" - {
        "01010100" {
            0L.expand(0b01010100).toList() shouldBe listOf(
                0b0000000L,
                0b0000100L,
                0b0010000L,
                0b0010100L,
                0b1000000L,
                0b1000100L,
                0b1010000L,
                0b1010100L,
            )
        }

        "examples" - {
            "X1001X expand 42" {
                Day14.Mask36.from("X1001X").expand(42)
                    .toList() shouldContainExactlyInAnyOrder listOf(26L, 27L, 58L, 59L)
            }

            "X0XX expand 26" {
                Day14.Mask36.from("X0XX").expand(26)
                    .toList() shouldContainExactlyInAnyOrder listOf(16, 17, 18, 19, 24, 25, 26, 27)
            }
        }
    }
})
