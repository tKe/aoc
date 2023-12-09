package year2023

import aok.PuzDSL
import aoksp.AoKSolution
import utils.lcm

fun main() = solveDay(8, warmup = aok.Warmup.iterations(500), runs=30)

@AoKSolution
object Day08 : PuzDSL({
    val parse = parser {
        val (route, net) = input.split("\n\n", limit = 2)
        route to net.trim().lines().associate {
            val (from, left, right) = it.split(" = (", ", ", ")")
            from to (left to right)
        }
    }

    part1(parse) { (route, net) ->
        var steps = 0
        var at = "AAA"
        while (at != "ZZZ") {
            when(route[steps++ % route.length]) {
                'R' -> at = net.getValue(at).second
                'L' -> at = net.getValue(at).first
            }
        }
        steps
    }

    part2(parse) { (route, net) ->
        val lengths = net.keys.filter { it.endsWith('A') }.map {
            var steps = 0
            var at = it
            while(!at.endsWith("Z")) {
                when(route[steps++ % route.length]) {
                    'R' -> at = net.getValue(at).second
                    'L' -> at = net.getValue(at).first
                }
            }
            steps.toLong()
        }
        lengths.reduce(::lcm)
    }
})

@AoKSolution
object Day08Immutable : PuzDSL({
    val parse = parser {
        val (route, net) = input.split("\n\n", limit = 2)
        route to net.trim().lines().associate {
            val (from, left, right) = it.split(" = (", ", ", ")")
            from to (left to right)
        }
    }

    part1(parse) { (route, net) ->
        sequence { while(true) yieldAll(route.asIterable()) }.runningFold("AAA") { at, n ->
            when(n) {
                'L' -> net.getValue(at).first
                'R' -> net.getValue(at).second
                else -> at
            }
        }.indexOf("ZZZ")
    }

    part2(parse) { (route, net) ->
        val lengths = net.keys.filter { it.endsWith('A') }.map {
            sequence { while(true) yieldAll(route.asIterable()) }.runningFold(it) { at, n ->
                when(n) {
                    'L' -> net.getValue(at).first
                    'R' -> net.getValue(at).second
                    else -> at
                }
            }.indexOfFirst { at -> at.endsWith('Z') }.toLong()
        }
        lengths.reduce(::lcm)
    }
})
