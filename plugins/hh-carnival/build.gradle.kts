plugins {
    id(GradlePlugins.gradleIntelliJPlugin)
    kotlin("jvm")
    id(GradlePlugins.setupIdeaPlugin)
}

repositories {
    mavenCentral()
    maven("https://packages.atlassian.com/maven/repository/public")
}

dependencies {
    implementation(project(":hh-plugins-core"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(Libs.freemarker)
    implementation("commons-io:commons-io:2.4")
    implementation("com.vladsch.flexmark:flexmark-all:0.50.42")
    implementation("com.atlassian.jira:jira-rest-java-client-core:4.0.0") {
        exclude(group = "org.slf4j")
        dependencies {
            implementation("com.atlassian.fugue:fugue:2.6.1")
        }
    }
}