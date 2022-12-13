package aoksp

import aok.PuzKey
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import kotlin.reflect.KProperty1

@Target(AnnotationTarget.CLASS)
annotation class AoKSolution(
    val year: Int = 0,
    val day: Int = 0,
    val variant: String = "",
    // TODO: input mapping per-part
)

private val annotationDefaults = AoKSolution()

context(KSAnnotated)
@OptIn(KspExperimental::class)
fun <T : Any> KProperty1<AoKSolution, T>.resolve(fallback: () -> T? = { null }): T {
    val annotation = getAnnotationsByType(AoKSolution::class).single()
    val annotations = generateSequence(parent, KSNode::parent).flatMap {
        (it as? KSAnnotated)?.getAnnotationsByType(AoKSolution::class).orEmpty()
    }.toList().toTypedArray()

    val default = get(annotationDefaults)
    return listOfNotNull(annotation, *annotations)
        .firstNotNullOfOrNull { get(it).takeUnless(default::equals) }
        ?: fallback()
        ?: error("not specified: ${this.name}")
}

context(KSPLogger)
internal fun Resolver.resolveSolutions() =
    getSymbolsWithAnnotation(AoKSolution::class.qualifiedName!!)
        .flatMap { annotated ->
            when (annotated) {
                is KSClassDeclaration -> {
                    val deets = annotated.resolveSolutionDetails()
                    val funcs = annotated.getDeclaredFunctions().associateBy { it.resolvePart() }
                    listOfNotNull(funcs[1], funcs[2]).map { deets to it }
                }

                else -> emptyList()
            }
        }
        .groupBy({ it.first }, { it.second })
        .map { (solution, functions) ->
            val part1 = functions.firstOrNull { it.resolvePart() == 1 }
            val part2 = functions.firstOrNull { it.resolvePart() == 2 }
            Triple(solution, part1, part2)
        }

private fun KSDeclaration.resolveSolutionDetails(): PuzKey = PuzKey.of(
    year = AoKSolution::year.resolve {
        packageName.asString().removePrefix("year").toIntOrNull()
    },
    day = AoKSolution::day.resolve {
        names().firstNotNullOfOrNull {
            it.lowercase().removePrefix("day")
                .takeWhile(Char::isDigit)
                .toIntOrNull()
        }
    },
    variant = AoKSolution::variant.resolve(::resolveVariant)
)

private val partSuffix = """part(?:[12]|one|two)$""".toRegex(RegexOption.IGNORE_CASE)
private val dayPrefix = """^day([012][0-9])""".toRegex(RegexOption.IGNORE_CASE)

private fun KSDeclaration.names() = generateSequence(this, KSNode::parent).mapNotNull {
    when (it) {
        is KSDeclaration -> it.simpleName.asString()
        is KSFile -> it.fileName.removeSuffix(".kt")
        else -> null
    }
}

private fun KSDeclaration.resolveVariant() = names()
    .map { it.replace(partSuffix, "").replace(dayPrefix, "") }
    .firstOrNull { it.isNotEmpty() }
    ?: "Default"

private fun KSFunctionDeclaration.resolvePart() =
    partSuffix.find(simpleName.asString())?.let {
        when (it.value.lowercase()) {
            "part1", "partone" -> 1
            "part2", "parttwo" -> 2
            else -> null
        }
    }
