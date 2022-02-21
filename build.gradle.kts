plugins {
    id("convention.static-analysis")
    id("ru.hh.plugins.gradle.build-all-plugins")
    id("ru.hh.plugins.gradle.collect-update-plugins")
}

staticAnalysis {
    detekt {
        configPath = files(project.rootDir.resolve("build-logic/static-analysis-convention/rules/detekt/detekt-config.yaml"))
    }
}
