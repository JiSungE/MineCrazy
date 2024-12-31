import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    `java-library`
    idea
    `maven-publish`
    id("net.neoforged.gradle.userdev") version "7.0.142"
}

version = project.property("mod_version") as String
group = project.property("mod_group_id") as String

repositories {
    mavenLocal()

    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

base {
    archivesName.set(project.property("mod_id") as String)
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

runs {
    configureEach {
        systemProperty("forge.logging.markers", "REGISTRIES")
        systemProperty("forge.logging.console.level", "debug")
        modSource(project.sourceSets.main.get())
    }

    create("client") {
        systemProperty("forge.enabledGameTestNamespaces", project.property("mod_id") as String)
    }

    create("server") {
        systemProperty("forge.enabledGameTestNamespaces", project.property("mod_id") as String)
        programArgument("--nogui")
    }

    create("gameTestServer") {
        systemProperty("forge.enabledGameTestNamespaces", project.property("mod_id") as String)
    }

    create("data") {
        programArguments.addAll(
            "--mod", project.property("mod_id") as String,
            "--all",
            "--output", file("src/generated/resources/").absolutePath,
            "--existing", file("src/main/resources/").absolutePath
        )
    }
}

sourceSets["main"].resources.srcDir("src/generated/resources")

dependencies {
    implementation("thedarkcolour:kotlinforforge-neoforge:5.6.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.21")

    implementation("net.neoforged:neoforge:${project.property("neo_version") as String}")
}

tasks.withType<ProcessResources>().configureEach {
    val replaceProperties = mapOf(
        "minecraft_version" to (project.property("minecraft_version") as String),
        "minecraft_version_range" to (project.property("minecraft_version_range") as String),
        "neo_version" to (project.property("neo_version") as String),
        "neo_version_range" to (project.property("neo_version_range") as String),
        "loader_version_range" to (project.property("loader_version_range") as String),
        "mod_id" to (project.property("mod_id") as String),
        "mod_name" to (project.property("mod_name") as String),
        "mod_license" to (project.property("mod_license") as String),
        "mod_version" to (project.property("mod_version") as String),
        "mod_authors" to (project.property("mod_authors") as String),
        "mod_description" to (project.property("mod_description") as String)
    )
    inputs.properties(replaceProperties)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(replaceProperties)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("file://${project.projectDir}/repo")
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    sourceCompatibility = "21"
    targetCompatibility = "21"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JvmTarget.JVM_21
    }

}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
