import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.github.tke"
version = "0.0.2022"

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        languageVersion = "1.8"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xcontext-receivers",
            "-opt-in=kotlin.time.ExperimentalTime",
        )
    }
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(libs.bundles.codegen)
}
