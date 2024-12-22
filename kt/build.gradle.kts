import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    alias(libs.plugins.jmh)
}

group = "com.github.tke"
version = "0.0.2022"

repositories {
    mavenCentral()
}

tasks.withType<Test> { useJUnitPlatform() }

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "${JavaVersion.VERSION_21}"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xcontext-receivers",
//            "-Xwhen-guards", // performance?
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=kotlin.time.ExperimentalTime",
            "-opt-in=kotlin.ExperimentalStdlibApi"
        )
    }
}

kotlin {
    jvmToolchain(21)
    sourceSets.main {
        kotlin.srcDir("generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("generated/ksp/test/kotlin")
    }
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

    testImplementation(libs.bundles.kotest)
}
