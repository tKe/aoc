package year2019

import aok.testAllSolutions
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.positiveLong
import io.kotest.property.checkAll
import year2019.Day22.Technique
import year2019.Day22.divMod
import year2019.Day22.parseShuffle
import year2019.Day22.timesMod

class Day22Test : FreeSpec({
    val shuffle = parseShuffle(10)
    fun Technique.toDeck() = List(cards.toInt()) { this.cardAt(it.toLong()) }

    "utils" - {
        "timesMod" {
            checkAll(Arb.long(min = 0), Arb.long(min = 0), Arb.positiveLong()) { a, b, mod ->
                timesMod(a, b, mod) shouldBe a.toBigInteger().times(b.toBigInteger()).mod(mod.toBigInteger()).toLong()
            }
        }

        "divMod" {
            divMod(8, 3, 5) shouldBe 1L
            divMod(11, 4, 5) shouldBe 4L
        }
    }

    "examples" - {
        "deal into new stack" {
            shuffle(testCase.name.name).toDeck()
                .shouldContainExactly(9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
        }
        "cut 3" {
            shuffle(testCase.name.name).toDeck()
                .shouldContainExactly(3, 4, 5, 6, 7, 8, 9, 0, 1, 2)
        }
        "cut -4" {
            shuffle(testCase.name.name).toDeck()
                .shouldContainExactly(6, 7, 8, 9, 0, 1, 2, 3, 4, 5)
        }
        "deal with increment 3" {
            shuffle(testCase.name.name).toDeck()
                .shouldContainExactly(0, 7, 4, 1, 8, 5, 2, 9, 6, 3)
        }
    }

    "combinations" - {
        "cut,deal,deal" {
            shuffle("cut 6\ndeal with increment 7\ndeal into new stack").toDeck()
                .shouldContainExactly(3, 0, 7, 4, 1, 8, 5, 2, 9, 6)
        }
        "deal,deal,cut" {
            shuffle("deal with increment 7\ndeal with increment 9\ncut -2").toDeck()
                .shouldContainExactly(6, 3, 0, 7, 4, 1, 8, 5, 2, 9)
        }
        "deal,stack,deal" {
            shuffle("deal with increment 7\ndeal into new stack\ndeal into new stack").toDeck()
                .shouldContainExactly(0, 3, 6, 9, 2, 5, 8, 1, 4, 7)
        }
        "long combination" {
            val recipe = """
                deal into new stack
                cut -2
                deal with increment 7
                cut 8
                cut -4
                deal with increment 7
                cut 3
                deal with increment 9
                deal with increment 3
                cut -1
            """.trimIndent()
            shuffle(recipe).toDeck()
                .shouldContainExactly(9, 2, 5, 8, 1, 4, 7, 0, 3, 6)
        }
    }

    include(queryDay(22).testAllSolutions(part1 = 6850L, part2 = 13224103523662))
})
