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
    implementation(project(":shared-core-utils"))
    implementation(project(":shared-core-ui"))
    implementation(project(":shared-core-freemarker"))

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(Libs.freemarker)
    implementation("commons-io:commons-io:2.4")
    implementation(Libs.flexmark)
    implementation("com.atlassian.jira:jira-rest-java-client-core:4.0.0") {
        exclude(group = "org.slf4j")
        dependencies {
            implementation("com.atlassian.fugue:fugue:2.6.1")
        }
    }
}