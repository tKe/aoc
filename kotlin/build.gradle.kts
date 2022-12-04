import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.github.tke.aoc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<Test> { useJUnitPlatform() }

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "${JavaVersion.VERSION_17}"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xcontext-receivers",
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=kotlin.time.ExperimentalTime",
        )
    }
}

val a by tasks.creating

dependencies {
    implementation(kotlin("reflect"))
    implementation(libs.bundles.arrow)
    implementation(libs.coroutines.core)
    testImplementation(libs.bundles.kotest)
}
