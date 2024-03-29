package aoksp

import aok.Puz
import aok.PuzKey
import aok.PuzzleInput
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec.Companion.classBuilder
import com.squareup.kotlinpoet.TypeSpec.Companion.objectBuilder
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

                val warmup = ClassName("aok", "Warmup")
                val inputScopeProvider = ClassName("aok", "InputProvider")
                FileSpec.builder("year$year", yearClass.name!!)
                    .addType(yearClass)
                    .addImport("aok", "sealedObjects", "warmup", "solveAll")
                    .addFunction(FunSpec.builder("queryDay")
                        .addParameter(ParameterSpec.builder("day", typeNameOf<Int?>()).defaultValue("null").build())
                        .addStatement("return %T::class.sealedObjects.filter { day == null || it.day == day }", yearClassName(year))
                        .returns(typeNameOf<List<Puz<*, *>>>())
                        .build())
                    .addFunction(FunSpec.builder("solveDay")
                        .addParameter(ParameterSpec.builder("day", typeNameOf<Int?>()).defaultValue("null").build())
                        .addParameter(ParameterSpec.builder("warmup", warmup).defaultValue("%M", warmup.nestedClass("Companion").member("none")).build())
                        .addParameter(ParameterSpec.builder("runs", Int::class).defaultValue("%L", 1).build())
                        .addParameter(ParameterSpec.builder("input", inputScopeProvider).defaultValue("%M", inputScopeProvider.member("Companion")).build())
                        .addCode("return with(input) { queryDay(day).warmup(warmup).solveAll(runs) }")
                        .build())

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

        val tvPart1 = part1?.returnType?.unaliased()?.toTypeName() ?: ANY
        val tvPart2 = part2?.returnType?.unaliased()?.toTypeName() ?: ANY

        addModifiers(KModifier.DATA)
        superclass(yearClassName(year).parameterizedBy(tvPart1, tvPart2))
        addSuperclassConstructorParameter("%L", day)
        addProperty(
            PropertySpec.builder(PuzKey::variant.name, String::class, KModifier.FINAL, KModifier.OVERRIDE)
                .initializer("%S", variant).build()
        )

        addFunctions(
            listOfNotNull(part1?.to("part1"), part2?.to("part2")).map { (target, func) ->
                val partImpl = FunSpec.builder(func)
                    .contextReceivers(PuzzleInput::class.asTypeName())
                    .returns(target.returnType?.unaliased()?.toTypeName() ?: ANY)
                    .addModifiers(KModifier.OVERRIDE)

                val targetMember = when (val parent = target.parent) {
                    is KSClassDeclaration ->
                        parent.toClassName().member(target.simpleName.asString())

                    is KSFile ->
                        MemberName(parent.packageName.asString(), target.simpleName.asString())

                    else -> null
                }

                if (targetMember == null) {
                    partImpl.addStatement("return TODO(%S)", "Unable to call $target (unsupported parent)")
                } else if (Modifier.SUSPEND in target.modifiers) {
                    partImpl.addStatement(
                        "return %M(%M) { %M() }",
                        MemberName("kotlinx.coroutines", "runBlocking"),
                        ClassName("kotlinx.coroutines", "Dispatchers").member("Default"),
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
