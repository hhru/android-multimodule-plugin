plugins {
    `kotlin-dsl`
    id("convention.libraries")
}

group = "ru.hh.plugins.static_analysis"

dependencies {
    implementation("ru.hh.plugins.build_logic:libraries")
    implementation(Libs.staticAnalysis.detektGradlePlugin)
    implementation(Libs.kotlinHtml)
}