plugins {
    id(GradlePlugins.gradleIntelliJPlugin)
    kotlin("jvm")
    id(GradlePlugins.setupIdeaPlugin)
}

repositories {
    mavenCentral()
}

dependencies {
    // Core modules
    implementation(project(":shared-core-ui"))
    implementation(project(":shared-core-utils"))
    implementation(project(":shared-core-freemarker"))
    implementation(project(":shared-core-code-modification"))

    // Feature modules
    implementation(project(":shared-feature-geminio-sdk"))

    // Libraries
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(Libs.flexmark)   // Markdown parser
}



tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
    1.0.1 -- Several new features: <br/>
        - Add new command for recipe: `addDependencies` for adding dependencies into build.gradle file <br/>
        - New hardcoded parameter in FTL-files: `applicationPackage` - 
            it is package name from AndroidManifest.xml file. <br/>
    
    <br/>
    1.0.0 -- Initial release <br/>
    """)
}