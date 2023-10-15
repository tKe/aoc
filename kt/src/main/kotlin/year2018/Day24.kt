package year2018

import aok.PuzDSL
import aoksp.AoKSolution

fun main(): Unit = solveDay(
    24,
//    input = aok.InputProvider.Example
)

@AoKSolution
object Day24 : PuzDSL({
    data class UnitGroup(
        val id: String,
        var units: Int,
        val hp: Int,
        val immuneTo: Set<String>,
        val weakTo: Set<String>,
        val attackType: String,
        val attackDamage: Int,
        val initiative: Int
    ) {
        val effectivePower get() = units * attackDamage
    }

    val parse = parser {
        val groupParser =
            Regex("""(?<units>\d+) units each with (?<hitpoints>\d+) hit points (?:[(]([^)]+)[)] )?with an attack that does (?<dmg>\d+) (?<attackType>[^ ]+) damage at initiative (?<initiative>\d+)""")
        fun String.parseDefenses() = split("; ").associate {
            val p = it.split(" to ", ", ")
            p.first() to p.drop(1).toSet()
        }.let {
            it["immune"].orEmpty() to it["weak"].orEmpty()
        }
        val (immune, infection) = input.split("\n\n").map { team ->
            val lines = team.lines()
            val icon = if (lines.first().startsWith("Imm")) "🛡" else "🦠"
            lines.drop(1)
                .map {
                    groupParser.matchEntire(it)?.destructured ?: error("failed to parse '$it'")
                }
                .mapIndexed { idx, (u, hp, def, ad, at, i) ->
                    val (im, wk) = def.parseDefenses()
                    UnitGroup(
                        id = icon + (idx + 1),
                        units = u.toInt(),
                        hp = hp.toInt(),
                        immuneTo = im,
                        weakTo = wk,
                        attackType = at,
                        attackDamage = ad.toInt(),
                        initiative = i.toInt()
                    )
                }
        }
        immune to infection
    }

    infix fun UnitGroup.damageDealtTo(target: UnitGroup) = effectivePower * when (attackType) {
        in target.immuneTo -> 0
        in target.weakTo -> 2
        else -> 1
    }

    infix fun UnitGroup.attack(target: UnitGroup) = minOf(target.units, damageDealtTo(target) / target.hp)

    val selectionOrder = compareByDescending(UnitGroup::effectivePower)
        .thenByDescending(UnitGroup::initiative)
    val targetOrder = compareByDescending<Pair<UnitGroup, Int>> { (_, dmg) -> dmg }
        .thenBy(selectionOrder) { (group) -> group }

    infix fun List<UnitGroup>.selections(defenders: List<UnitGroup>) = sequence {
        val remainingDefenders = defenders.filterTo(mutableListOf()) { it.units > 0 }
        for (attacker in sortedWith(selectionOrder)) {
            val (target, damage) = remainingDefenders.minOfWith(targetOrder) {
                it to attacker.damageDealtTo(it)
            }
            if (damage > 0) {
                remainingDefenders -= target
                yield(attacker to target)
            }
            if (remainingDefenders.isEmpty()) break
        }
    }

    fun fight(immune: List<UnitGroup>, infection: List<UnitGroup>): Pair<Int, Int> {
        while (immune.any { it.units > 0 } && infection.any { it.units > 0 }) {
            val selections = ((infection selections immune) + (immune selections infection))
                .sortedByDescending { it.first.initiative }
            var totalKilled = 0
            for ((attacker, defender) in selections) {
                val killed = attacker attack defender
                defender.units -= killed
                totalKilled += killed
            }
            if(totalKilled == 0) break
        }
        return immune.sumOf(UnitGroup::units) to infection.sumOf(UnitGroup::units)
    }

    part1 {
        val (immune, infection) = parse()
        fight(immune, infection)
        (immune + infection).sumOf { it.units }
    }

    part2 {
        val (immune, infection) = parse()
        fun List<UnitGroup>.clone(boost: Int = 0) = map { it.copy(attackDamage = it.attackDamage + boost) }
        // TODO: binary-search?
        (1..Int.MAX_VALUE).firstNotNullOf { boost ->
            val (score, other) = fight(immune.clone(boost), infection.clone())
            score.takeIf { other == 0 }
        }
    }

})
