plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.fassykite"
version = "1.0.0"
description = "PolarisX - Advanced Anti-Cheat System"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")

    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.3")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("PolarisX-${version}.jar")
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }
}