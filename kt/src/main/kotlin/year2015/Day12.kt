package year2015

import aok.PuzDSL
import aoksp.AoKSolution
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*


@AoKSolution
object Day12 : PuzDSL({
    part1 {
        input.split("""[^0-9-]+""".toRegex()).mapNotNull(String::toIntOrNull).sum()
    }

    val red = JsonPrimitive("red")
    part2 {
        lines.map<String, JsonElement>(Json::decodeFromString)
            .sumOf(DeepRecursiveFunction<JsonElement, Int> { element ->
                when (element) {
                    is JsonPrimitive -> element.intOrNull ?: 0
                    is JsonArray -> element.sumOf { callRecursive(it) }
                    is JsonObject -> {
                        if (element.containsValue(red)) 0
                        else element.values.sumOf { callRecursive(it) }
                    }
                }
            }::invoke)
    }
})

fun main() = solveDay(12)
