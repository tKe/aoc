package year2020

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import year2020.Day24.HexCell
import year2020.Day24.HexMove.*
import year2020.Day24.print

class Day24Spec : FreeSpec({

    "sequences" {
        Day24.HexMove.moves("nwwswee").toList() shouldContainExactly listOf(NorthWest, West, SouthWest, East, East)
    }
    "identity" {
        HexCell() + Day24.HexMove.moves("nwwswee") shouldBe HexCell()
    }





    "moves" - {
        "se" {
            HexCell() + Day24.HexMove.SouthEast shouldBe HexCell(-1)
        }
    }

    "cartesian" - {
        "se" {
            HexCell.fromXY(1, 1).should {
                it.x shouldBe 1
                it.y shouldBe 1
            }
        }
    }

    "steps" {
        var cur = HexCell()
        val moves = Day24.HexMove.moves("sesenwnenenewseeswwswswwnenewsewsw")
        buildSet {
            for (move in moves) {
                add(cur)
                val prev = cur
                cur += move
                println("moved from $prev -$move-> $cur")
                print(
                    prev::equals to "🟡",
                    cur::equals to "🔴",
                    HexCell()::equals to "🟢",
                )
            }
        }
    }


})
