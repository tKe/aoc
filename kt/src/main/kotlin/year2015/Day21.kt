package year2015

import aok.PuzDSL
import aok.PuzzleInput
import aoksp.AoKSolution
import kotlin.math.ceil

@AoKSolution
object Day21 : PuzDSL({
    data class Item(val name: String, val cost: Int, val damage: Int, val armour: Int)
    data class Attacker(val hitPoints: Int, val damage: Int, val armour: Int)

    val shop = object {
        val weapons = listOf(
            Item("Dagger", 8, 4, 0),
            Item("Shortsword", 10, 5, 0),
            Item("Warhammer", 25, 6, 0),
            Item("Longsword", 40, 7, 0),
            Item("Greataxe", 74, 8, 0),
        )
        val armours = listOf(
            Item("Leather", 13, 0, 1),
            Item("Chainmail", 31, 0, 2),
            Item("Splintmail", 53, 0, 3),
            Item("Bandedmail", 75, 0, 4),
            Item("Platemail", 102, 0, 5),
        )
        val rings = listOf(
            Item("Damage +1", 25, 1, 0),
            Item("Damage +2", 50, 2, 0),
            Item("Damage +3", 100, 3, 0),
            Item("Defense +1", 20, 0, 1),
            Item("Defense +2", 40, 0, 2),
            Item("Defense +3", 80, 0, 3),
        )

        val loadouts = buildList {
            val armourOptions = armours.map(::listOf) + listOf(emptyList())
            val ringOptions = rings.withIndex().flatMap { (idx, ring1) ->
                rings.drop(idx + 1).map { listOf(ring1, it) }
            } + rings.map(::listOf) + listOf(emptyList())
            weapons.forEach { weapon ->
                armourOptions.forEach { armourOption ->
                    ringOptions.forEach { ringOption ->
                        add(ringOption + armourOption + weapon)
                    }
                }
            }
        }.sortedBy { it.sumOf(Item::cost) }
    }

    infix fun Attacker.hitsToKill(other: Attacker) = ceil(other.hitPoints.toDouble() / maxOf(1, damage - other.armour))
    infix fun Attacker.beats(other: Attacker) = this hitsToKill other <= other hitsToKill this

    fun List<Item>.equip() = Attacker(100, sumOf(Item::damage), sumOf(Item::armour))

    fun PuzzleInput.boss() = input.split(' ', '\n')
        .mapNotNull(String::toIntOrNull)
        .let { (hp, dmg, arm) -> Attacker(hp, dmg, arm) }

    part1 {
        val boss = boss()

        shop.loadouts
            .sortedBy { it.sumOf(Item::cost) }
            .first { it.equip() beats boss }
            .sumOf(Item::cost)
    }

    part2 {
        val boss = boss()

        shop.loadouts
            .last { !(it.equip() beats boss) }
            .sumOf(Item::cost)
    }
})


fun main() = solveDay(
    21,
)
