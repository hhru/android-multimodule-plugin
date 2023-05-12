plugins {
    `kotlin-dsl`
    id("convention.build.logic.plugin")
}

group = "ru.hh.plugins.build_logic"

dependencies {
    implementation("ru.hh.plugins.build_logic:libraries")
    implementation("ru.hh.plugins.build_logic:testing-convention")
    implementation("ru.hh.plugins.build_logic:kotlin-convention")

    implementation(Libs.gradleIntelliJPlugin)
    implementation(Libs.gradleChangelogPlugin)
    implementation(Libs.kotlinPlugin)
}
