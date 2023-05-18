plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.runicrealms.plugin"
version = "1.0-SNAPSHOT"

dependencies {
    compileOnly(commonLibs.spigot)
    compileOnly(commonLibs.craftbukkit)
    implementation(commonLibs.acf)
    compileOnly(commonLibs.paper)
    compileOnly(project(":Projects:Chat"))
    compileOnly(project(":Projects:Core"))
    compileOnly(project(":Projects:Items"))
    compileOnly(project(":Projects:Npcs"))
    compileOnly(project(":Projects:Restart"))
    compileOnly(commonLibs.holographicdisplays)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.runicrealms.plugin"
            artifactId = "professions"
            version = "1.0-SNAPSHOT"
            from(components["java"])
        }
    }
}

tasks.register("wrapper")
tasks.register("prepareKotlinBuildScriptModel")