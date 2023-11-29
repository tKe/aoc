package year2020

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.sequences.shouldHaveSize
import io.kotest.matchers.shouldBe
import year2020.Day20.zipConcat

class Day20Spec : FreeSpec({
    "tile" - {
        fun String.asTile() = Day20.Tile(split('/'))
        fun Day20.Tile.asString() = content.joinToString("/")
        "flipping" - {
            "no-flip" {
                "abc/def/ghi".asTile().flip().asString() shouldBe "abc/def/ghi"
            }
            "flip-x" {
                "abc/def/ghi".asTile().flip(x = true).asString() shouldBe "cba/fed/ihg"
            }
            "flip-y" {
                "abc/def/ghi".asTile().flip(y = true).asString() shouldBe "ghi/def/abc"
            }
            "swap-xy" {
                "abc/def/ghi".asTile().flip(swapXY = true).asString() shouldBe "adg/beh/cfi"
            }
            "unique" {
                "abc/def/ghi".asTile().orientations.distinctBy { it.asString() } shouldHaveSize 8
            }
        }
    }

    "zipConcat" - {
        listOf("abc/def/ghi".split("/"), "jkl/mno/pqr".split("/")).zipConcat()
            .shouldContainInOrder("abcjkl", "defmno", "ghipqr")
    }

})
