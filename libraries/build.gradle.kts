plugins {
    `kotlin-dsl`
}

group = "ru.hh.plugins.build_logic"

kotlin {
    @Suppress("MagicNumber")
    jvmToolchain(11)
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
}
