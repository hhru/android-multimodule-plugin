import ru.hh.plugins.static_analysis.StaticAnalysisExtension

extensions.create<StaticAnalysisExtension>("staticAnalysis")

plugins {
    id("convention.static-analysis.detekt")
}

tasks.create("staticAnalysis") {
    dependsOn("detektAll")
}

