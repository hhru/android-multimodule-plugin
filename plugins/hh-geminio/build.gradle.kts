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
    1.1.3 -- Remove 'choose module' step from modules actions <br/>
    <br/>
        
    1.1.2 -- Little fixes: <br/>
        - Made 'help' parameter optional in widgets section <br/>
        - Moved templates actions to the top of 'New' group <br/>
    <br/>    
    
    1.1.1 -- Fix bug with duplicated project service. <br/>    
    <br/>
        
    1.1.0 -- Adding support for Modules creation and another cool stuff: <br/>
        - Added a lot of validation messages for recipes to help you properly create new recipes <br/>
        - Added new command for recipe: `mkDirs` for creating directories structure <br/>
        - New predefined variables in `recipe` section: {manifestOut} and {rootOut} <br/>
    <br/>   
    
    1.0.1 -- Several new features: <br/>
        - Add new command for recipe: `addDependencies` for adding dependencies into build.gradle file <br/>
        - New hardcoded parameter in FTL-files: `applicationPackage` - 
            it is package name from AndroidManifest.xml file. <br/>
    
    <br/>
    
    1.0.0 -- Initial release <br/>
    """)
}