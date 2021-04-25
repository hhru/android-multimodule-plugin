plugins {
    `kotlin-dsl`
    id("convention.libraries")
}

group = "ru.hh.plugins.build_logic"

dependencies {
    implementation("ru.hh.plugins.build_logic:libraries")
    implementation("ru.hh.plugins.build_logic:testing-convention")
    implementation(Libs.kotlinPlugin)
}