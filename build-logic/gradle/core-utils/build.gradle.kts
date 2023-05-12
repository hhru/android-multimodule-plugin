plugins {
    `kotlin-dsl`
    id("convention.build.logic.plugin")
}

group = "ru.hh.plugins.gradle"

dependencies {
    implementation("ru.hh.plugins.build_logic:libraries")
}
