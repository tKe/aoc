plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.github.tke"
version = "0.0.2022"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xcontext-receivers",
        )
    }
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(libs.bundles.codegen)
}
