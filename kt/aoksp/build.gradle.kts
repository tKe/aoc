plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.github.tke"
version = "0.0.2025"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xcontext-parameters",
        )
    }
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(libs.bundles.codegen)
}
