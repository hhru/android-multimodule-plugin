plugins {
    `kotlin-dsl`
    id("convention.libraries")
}

group = "ru.hh.plugins.gradle"

dependencies {
    implementation("ru.hh.plugins.build_logic:libraries")
}
