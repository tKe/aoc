package year2020

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(19)

@AoKSolution
object Day19 : PuzDSL({
    val parse = parser {
        val (rules, messages) = input.split("\n\n", limit = 2)

        rules.lineSequence().associate {
            val (id, rule) = it.split(": ", limit = 2)
            id.toInt() to Rule(rule)
        } to messages.lines()
    }

    part1(parse) { (rules, messages) ->
        fun Rule.regexString(): String = when (this) {
            is Const -> Regex.escape(value)
            is Ref -> rules.getValue(id).regexString()
            is Chain -> this.rules.joinToString("") { it.regexString() }
            is Choice -> this.rules.joinToString("|", "(?:", ")") { it.regexString() }
        }
        messages.count(Ref(0).regexString().toRegex()::matches)
    }

    part2(parse) { (rules, messages) ->
        fun Rule.regexString(): String = when (this) {
            is Const -> value
            is Ref -> when (id) {
                8 -> "(?:${rules.getValue(42).regexString()})+"
                11 -> {
                    val r42 = rules.getValue(42).regexString()
                    val r31 = rules.getValue(31).regexString()
                    // poor-man's recursive regex...
                    // at most 10 deep for now - seems to be enough for my input.
                    (1..10).joinToString("|", "(?:", ")") {
                        buildString {
                            repeat(it) { append(r42) }
                            repeat(it) { append(r31) }
                        }
                    }
                }

                else -> rules.getValue(id).regexString()
            }

            is Chain -> this.rules.joinToString("") { it.regexString() }
            is Choice -> this.rules.joinToString("|", "(?:", ")") { it.regexString() }
        }
        messages.count(Ref(0).regexString().toRegex()::matches)
    }
}) {
    sealed interface Rule {
        companion object {
            operator fun invoke(repr: String): Rule = repr.split(" | ").let { choices ->
                when (choices.size) {
                    1 -> choices.single().split(" ")
                        .map { it.toIntOrNull()?.let(::Ref) ?: Const(it.trim('"')) }
                        .let { comps ->
                            when (comps.size) {
                                1 -> comps.single()
                                else -> Chain(comps)
                            }
                        }

                    else -> Choice(choices.map(::invoke))
                }
            }
        }
    }

    data class Const(val value: String) : Rule
    data class Ref(val id: Int) : Rule
    data class Choice(val rules: List<Rule>) : Rule
    data class Chain(val rules: List<Rule>) : Rule
}