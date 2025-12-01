plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    alias(libs.plugins.jmh)
}

group = "com.github.tke"
version = "0.0.2025"

repositories {
    mavenCentral()
}

tasks.withType<Test> { useJUnitPlatform() }

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xcontext-parameters",
        )
        optIn.addAll(
            "kotlinx.coroutines.FlowPreview",
            "kotlin.time.ExperimentalTime",
            "kotlin.ExperimentalStdlibApi"
        )
    }
    sourceSets.main {
        kotlin.srcDir("generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("generated/ksp/test/kotlin")
    }
    jvmToolchain(21)
}

jmh {
    includeTests = false
    fork = 1
    iterations = 3
    warmup = "1s"
    timeOnIteration = "2s"
    benchmarkMode.addAll("avgt", "ss", "thrpt")
    timeUnit = "us"
    includes.add("LongSplit")
}

dependencies {
    ksp("com.github.tke:aoksp")
    implementation("com.github.tke:aoksp")

    implementation(kotlin("reflect"))
    implementation(libs.bundles.arrow)
    implementation(libs.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.kotlinx.datetime)
    implementation(libs.jimfs) // year-22 day-07 virtual filesystem
    implementation("org.openjdk.jmh:jmh-core:1.37")
    implementation(libs.fastutils)

    testImplementation(libs.bundles.kotest)
}
