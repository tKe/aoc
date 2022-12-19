package aoksp

import aok.Puz
import aok.PuzKey
import aok.PuzzleInput
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeSpec.Companion.classBuilder
import com.squareup.kotlinpoet.TypeSpec.Companion.objectBuilder
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

private val String.upperCamel: String
    get() = split(' ').joinToString("") { it[0].uppercase() + it.drop(1) }

class AdventOfKotlinSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        AdventOfKotlinSymbolProcessor(environment.logger, environment.codeGenerator)
}

class AdventOfKotlinSymbolProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    override fun process(resolver: Resolver) = with(logger) {
        resolver.resolveSolutions()
            .forEachGroup({ (s) -> s.year }) { year, days ->
                val yearClass = generateYearClass(year)
                logger.info("generating $yearClass")

                FileSpec.builder("year$year", yearClass.name!!)
                    .addType(yearClass)
                    .build()
                    .writeTo(codeGenerator, Dependencies.ALL_FILES)

                days.forEachGroup({ (s) -> s.day }) { day, solutions ->
                    val dayFile = FileSpec.builder("year$year", "AoKDay${day.toString().padStart(2, '0')}")
                    solutions.forEach { (def, part1, part2) ->
                        dayFile.addType(def.generateSolutionClass(part1, part2))
                    }
                    dayFile.build().writeTo(codeGenerator, Dependencies.ALL_FILES)
                }
            }
        emptyList<KSAnnotated>()
    }

    private fun generateYearClass(year: Int): TypeSpec {
        val tvPart1 = TypeVariableName("Part1")
        val tvPart2 = TypeVariableName("Part2")
        return classBuilder(yearClassName(year)).apply {
            addModifiers(KModifier.SEALED)
            addTypeVariable(tvPart1)
            addTypeVariable(tvPart2)
            addSuperinterface(Puz::class.asTypeName().parameterizedBy(tvPart1, tvPart2))

            primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("day", Int::class)
                    .build()
            )
            addProperty(
                PropertySpec.builder("day", Int::class, KModifier.FINAL, KModifier.OVERRIDE)
                    .initializer("day").build()
            )

            addProperty(
                PropertySpec.builder("year", Int::class, KModifier.FINAL, KModifier.OVERRIDE)
                    .initializer("$year").build()
            )
        }.build()
    }


    private fun KSTypeReference.unaliased(): KSType = with(resolve()) {
        when (val decl = declaration) {
            is KSTypeAlias -> decl.type.resolve()
            else -> this
        }
    }

    @OptIn(ExperimentalKotlinPoetApi::class)
    private fun PuzKey.generateSolutionClass(
        part1: KSFunctionDeclaration?,
        part2: KSFunctionDeclaration?,
    ) = objectBuilder("PuzYear${year}Day$day${variant.upperCamel}").apply {
        superclass(
            yearClassName(year).parameterizedBy(
                part1?.returnType?.unaliased()?.toTypeName() ?: Any::class.asTypeName(),
                part2?.returnType?.unaliased()?.toTypeName() ?: Any::class.asTypeName()
            )
        )
        addSuperclassConstructorParameter("%L", day)
        addProperty(
            PropertySpec.builder(PuzKey::variant.name, String::class, KModifier.FINAL, KModifier.OVERRIDE)
                .initializer("%S", variant).build()
        )

        addFunctions(
            listOfNotNull(part1 to "part1", part2 to "part2").map { (target, func) ->
                val partImpl = FunSpec.builder(func)
                    .contextReceivers(PuzzleInput::class.asTypeName())
                    .addModifiers(KModifier.OVERRIDE)

                val targetMember = when (val parent = target?.parent) {
                    is KSClassDeclaration ->
                        parent.toClassName().member(target.simpleName.asString())

                    is KSFile ->
                        MemberName(parent.packageName.asString(), target.simpleName.asString())

                    else -> null
                }

                if (target == null) {
                    partImpl.addStatement("return TODO(%S)", "No implementation for $func")
                } else if (targetMember == null) {
                    partImpl.addStatement("return TODO(%S)", "Unable to call $target (unsupported parent)")
                } else if (Modifier.SUSPEND in target.modifiers) {
                    partImpl.addStatement(
                        "return %M(%M) { %M() }",
                        MemberName("kotlinx.coroutines", "runBlocking"),
                        MemberName(ClassName("kotlinx.coroutines", "Dispatchers"), "Default"),
                        targetMember
                    )
                } else {
                    partImpl.addStatement("return %M()", targetMember)
                }

                partImpl.build()
            })
    }.build()

    private fun yearClassName(year: Int) = ClassName("year$year", "AoKYear$year")
}

private fun <T, K> Iterable<T>.forEachGroup(
    key: (T) -> K,
    block: (K, List<T>) -> Unit,
) = groupBy { key(it) }.forEach(block)