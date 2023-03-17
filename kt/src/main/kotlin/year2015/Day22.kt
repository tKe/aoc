package year2015

import aok.PuzDSL
import aoksp.AoKSolution
import java.util.PriorityQueue

@AoKSolution
object Day22 : PuzDSL({

    data class Player(val hp: Int = 50, val mana: Int = 500, val spent: Int = 0) {
        fun spendMana(n: Int) = copy(mana = mana - n, spent = spent + n)
    }

    data class Boss(val hp: Int = 71, val dmg: Int = 10)

    data class Timers(val shield: Int = 0, val poison: Int = 0, val recharge: Int = 0) {
        operator fun dec() = Timers(
            if (shield > 0) shield - 1 else 0,
            if (poison > 0) poison - 1 else 0,
            if (recharge > 0) recharge - 1 else 0
        )
    }

    data class Match(
        val player: Player = Player(),
        val boss: Boss = Boss(),
        val timers: Timers = Timers()
    )

    fun Match.tickTimers() = Match(
        player = if (timers.recharge > 0) player.copy(mana = player.mana + 101) else player,
        boss = if (timers.poison > 0) boss.copy(hp = boss.hp - 3) else boss,
        timers = timers.dec()
    )

    fun Match.turn(attack: (Match) -> Match) = attack(tickTimers())

    fun Match.bossTurn() = if (boss.hp > 0) turn {
        if (it.boss.hp > 0) {
            val damage = maxOf(1, it.boss.dmg - if (it.timers.shield > 0) 7 else 0)
            it.copy(player = it.player.copy(hp = it.player.hp - damage))
        } else it
    } else this

    fun Match.castOrNull(mana: Int, predicate: Match.() -> Boolean = { true }, effect: Match.() -> Match) =
        if (player.mana >= mana && predicate()) copy(player = player.spendMana(mana)).effect() else null

    fun Match.nextStates() = sequence {
        tickTimers().run {
            yieldAll(
                setOfNotNull(
                    castOrNull(53) { copy(boss = boss.copy(hp = boss.hp - 4)) },
                    castOrNull(73) {
                        copy(boss = boss.copy(hp = boss.hp - 2), player = player.copy(hp = player.hp + 2))
                    },
                    castOrNull(113, { timers.shield == 0 }) { copy(timers = timers.copy(shield = 6)) },
                    castOrNull(173, { timers.poison == 0 }) { copy(timers = timers.copy(poison = 6)) },
                    castOrNull(229, { timers.recharge == 0 }) { copy(timers = timers.copy(recharge = 5)) },
                ).map { it.bossTurn() })
        }
    }.filter { it.player.hp > 0 }

    fun findMinMana(turns: Match.() -> Sequence<Match> = Match::nextStates): Int {
        val queue = PriorityQueue(compareBy<Match> { it.player.spent }).also { it += Match() }
        val visited = mutableSetOf<Match>()
        while (queue.isNotEmpty()) {
            val match = queue.poll()
            for (next in match.turns()) {
                if (next.boss.hp <= 0) return next.player.spent
                if (visited.add(next)) queue += next
            }
        }
        return -1
    }

    part1 {
        findMinMana()
    }

    part2 {
        findMinMana { copy(player = player.copy(hp = player.hp - 1)).nextStates() }
    }
})

fun main() = solveDay(
    22,
)
