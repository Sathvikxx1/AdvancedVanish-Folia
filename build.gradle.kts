plugins {
    kotlin("jvm") version "2.1.0"
    id("com.gradleup.shadow") version "9.0.0-beta10"
}

group = "me.quantiom"
version = "1.3.0"
description = "A fully customizable and advanced vanish plugin."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
    jvmToolchain(21)
}

sourceSets {
    main {
        java {
            srcDirs("src/main/java", "src/main/kotlin")
        }
    }
}
repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    maven("https://repo.essentialsx.net/releases/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://nexus.scarsz.me/content/groups/public/")
    maven("https://repo.codemc.org/repository/maven-community/")
    maven("https://repo.mikeprimm.com/")
    maven("https://repo.alessiodp.com/releases/")
    maven("https://repo.rosewooddev.io/repository/public-releases/")
    maven("https://repo.md-5.net/content/groups/public/")
    maven("https://repo.tcoded.com/releases")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")

    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    compileOnly("net.essentialsx:EssentialsX:2.20.1")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.discordsrv:discordsrv:1.28.0")
    compileOnly("us.dynmap:DynmapCoreAPI:3.6")
    compileOnly("us.dynmap:dynmap-api:3.6")
    compileOnly("xyz.jpenilla:squaremap-api:1.2.7")
    compileOnly("LibsDisguises:LibsDisguises:10.0.44")
    compileOnly("dev.esophose:playerparticles:8.6")

    implementation("redis.clients:jedis:5.1.1")
    implementation("org.jetbrains.exposed:exposed-core:0.47.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.47.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.47.0")
    implementation("net.byteflux:libby-bukkit:1.3.0")
    implementation("com.tcoded:FoliaLib:0.5.1")

    testImplementation(kotlin("test"))
}

tasks {
    shadowJar {
        archiveFileName.set("AdvancedVanish-$version.jar")

        relocate("redis.clients", "me.quantiom.advancedvanish.libs.redis.clients")
        relocate("org.jetbrains.exposed", "me.quantiom.advancedvanish.libs.exposed")
        relocate("net.byteflux", "me.quantiom.advancedvanish.libs.byteflux")
        relocate("co.aikar", "me.quantiom.advancedvanish.libs.aikar")
        relocate("com.tcoded.folialib", "me.quantiom.advancedvanish.libs.scheduler")

        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
        exclude("META-INF/LICENSE*", "META-INF/NOTICE*", "META-INF/DEPENDENCIES")
    }

    build {
        dependsOn(shadowJar)
    }
}
