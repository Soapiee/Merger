plugins {
    java
    id("io.freefair.lombok") version "9.4.0"
    id("com.gradleup.shadow") version "9.4.1"
}

version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:26.1-R0.1-SNAPSHOT")
    implementation("com.zaxxer:HikariCP:7.1.0")
    implementation("org.bstats:bstats-bukkit:3.2.1")
    compileOnly("me.clip:placeholderapi:2.12.2")
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
//    withSourcesJar()
//    withJavadocJar()
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        archiveFileName.set("Merger-${version}.jar")

        relocate("org.bstats", "me.soapiee.merger.libs.bstats")
        relocate("com.zaxxer.hikari", "me.soapiee.merger.libs.hikari")
    }

//    compileTestJava {
//        options.encoding = "UTF-8"
//    }

//    javadoc {
//        options.encoding = "UTF-8"
//    }

    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
