package aoksp

import aok.PuzKey
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.metadata.ClassKind
import kotlin.reflect.KProperty1

@Target(AnnotationTarget.CLASS)
annotation class AoKSolution(
    val year: Int = 0,
    val day: Int = 0,
    val variant: String = "",
    // TODO: input mapping per-part
)

private val annotationDefaults = AoKSolution()

context(annotated: KSAnnotated)
@OptIn(KspExperimental::class)
fun <T : Any> KProperty1<AoKSolution, T>.resolve(fallback: () -> T? = { null }): T {
    val annotation = annotated.getAnnotationsByType(AoKSolution::class).single()
    val annotations = generateSequence(annotated.parent, KSNode::parent).flatMap {
        (it as? KSAnnotated)?.getAnnotationsByType(AoKSolution::class).orEmpty()
    }.toList().toTypedArray()

    val default = get(annotationDefaults)
    return listOfNotNull(annotation, *annotations)
        .firstNotNullOfOrNull { get(it).takeUnless(default::equals) }
        ?: fallback()
        ?: error("not specified: ${this.name}")
}

internal fun Resolver.resolveSolutions() =
    getSymbolsWithAnnotation(AoKSolution::class.qualifiedName!!)
        .flatMap { annotated ->
            when (annotated) {
                is KSClassDeclaration -> {
                    require(annotated.classKind == com.google.devtools.ksp.symbol.ClassKind.OBJECT)
                    val deets = annotated.resolveSolutionDetails()
                    val funcs = annotated.getAllFunctions()
                        .associateBy { it.resolvePart() }
                    listOfNotNull(funcs[1], funcs[2]).map { Triple(annotated, deets, it) }
                }

                else -> emptyList()
            }
        }
        .groupBy({ it.second }, { it.first to it.third })
        .map { (solution, functions) ->
            val part1 = functions.firstOrNull { it.second.resolvePart() == 1 }
            val part2 = functions.firstOrNull { it.second.resolvePart() == 2 }

            Solution(solution, part1?.first ?: part2?.first, part1?.second, part2?.second)
        }

data class Solution(
    val key: PuzKey,
    val parent: KSDeclaration?,
    val part1: KSFunctionDeclaration?,
    val part2: KSFunctionDeclaration?,
)

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
