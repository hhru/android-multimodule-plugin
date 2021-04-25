plugins {
    id("convention.idea-plugin")
}

// TODO [build-logic] Look with a fresh eye, why this needs to be duplicated, if there is common dependency resolution in settings.gradle
repositories {
    mavenCentral()
    maven("https://packages.atlassian.com/maven/repository/public")
}

dependencies {
    implementation(project(":shared:core:utils"))
    implementation(project(":shared:core:ui"))
    implementation(project(":shared:core:freemarker"))
    implementation(project(":shared:core:code-modification"))

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("com.atlassian.jira:jira-rest-java-client-core:4.0.0") {
        exclude(group = "org.slf4j")
        dependencies {
            implementation("com.atlassian.fugue:fugue:2.6.1")
        }
    }
}