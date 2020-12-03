plugins {
    id(GradlePlugins.gradleIntelliJPlugin)
    kotlin("jvm")
    id(GradlePlugins.setupIdeaPlugin)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":hh-plugins-core"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(Libs.freemarker)

    testImplementation(Libs.tests.kotest) // for kotest framework
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
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