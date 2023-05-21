plugins {
    `java-library`
    `maven-publish`
}

group = "com.runicrealms.plugin"
version = "1.0-SNAPSHOT"
val artifactName = "professions"

dependencies {
    compileOnly(commonLibs.spigot)
    compileOnly(commonLibs.craftbukkit)
    compileOnly(commonLibs.acf)
    compileOnly(commonLibs.taskchain)
    compileOnly(commonLibs.paper)
    compileOnly(commonLibs.jedis)
    compileOnly(commonLibs.springdatamongodb)
    compileOnly(commonLibs.mongodbdriversync)
    compileOnly(commonLibs.mongodbdrivercore)
    compileOnly(commonLibs.mythicmobs)
    compileOnly(commonLibs.holographicdisplays)
    compileOnly(project(":Projects:Chat"))
    compileOnly(project(":Projects:Core"))
    compileOnly(project(":Projects:Items"))
    compileOnly(project(":Projects:Npcs"))
    compileOnly(project(":Projects:Restart"))
    compileOnly(project(":Projects:Database"))
    compileOnly(project(":Projects:Common"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.runicrealms.plugin"
            artifactId = artifactName
            version = "1.0-SNAPSHOT"
            from(components["java"])
        }
    }
}