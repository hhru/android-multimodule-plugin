plugins {
    id("convention.static-analysis")
}

staticAnalysis {
    detekt {
        configPath = files(project.rootDir.resolve("build-logic/static-analysis-convention/rules/detekt/detekt-config.yaml"))
    }
}