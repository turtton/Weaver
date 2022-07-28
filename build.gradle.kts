import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("fabric-loom") version "0.12-SNAPSHOT"
    `maven-publish`
    kotlin("jvm") version "1.7.10"
    scala
}
val maven_group: String by project
val minecraft_version: String by project
val build_number = System.getenv("BUILD_NUMBER") ?: "local"
version = "$minecraft_version+build.$build_number"
group = maven_group

val archives_base_name: String by project

base {
    archivesName.set(archives_base_name)
}

val gameTest = "gametest"
sourceSets {
    val main = main.get()
    create(gameTest) {
        compileClasspath += main.compileClasspath
        compileClasspath += main.output
        runtimeClasspath += main.runtimeClasspath
        runtimeClasspath += main.output
    }
}
val gameTestSource = sourceSets.getByName(gameTest)

// configurations {
//    getByName("${gameTest}Implementation") {
//        extendsFrom(testImplementation.get())
//    }
// }

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    mavenCentral()
}

val yarn_mappings: String by project
val loader_version: String by project
val fabric_version: String by project
dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings("net.fabricmc:yarn:$yarn_mappings:v2")
    modImplementation("net.fabricmc:fabric-loader:$loader_version")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")

    modImplementation(group = "net.fabricmc", name = "fabric-language-kotlin", version = "1.8.2+kotlin.1.7.10")
//    modImplementation(group = "net.fabricmc", name = "fabric-language-scala", version = "1.1.0+scala.2.13.6")
    implementation("org.scala-lang:scala3-library_3:3.1.2")

//    testImplementation("org.hamcrest:hamcrest:2.2")
//    testImplementation("io.kotest:kotest-assertions-core:5.2.1")
}

loom {
    accessWidenerPath.set(file("src/main/resources/weaver.accesswidener"))

    runs {
        create(gameTest) {
            server()
            configName = gameTest
            vmArgs += "-Dfabric-api.gametest"
            vmArgs += "-Dfabric.api.gametest.report-file=${project.buildDir}/junit.xml"
            runDir = "build/$gameTest"
            setSource(gameTestSource)
            isIdeConfigGenerated = true
        }
    }
}

@Suppress("UnstableApiUsage")
tasks.getByName<ProcessResources>("processResources") {
    inputs.property("version", project.version)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "loader_version" to loader_version,
            "minecraft_version" to minecraft_version
        )
    }
}

val targetJavaVersion = 16
tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = targetJavaVersion.toString()
}

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

tasks.getByName<Jar>("jar") {
    from("LICENSE") {
        rename { "${it}_$archives_base_name" }
    }
}

tasks.withType<ScalaCompile> {
    classpath += files(tasks.getByName<KotlinCompile>("compileKotlin").destinationDirectory)
}

// configure the maven publication
publishing {
    publications {
        create("mavenJava", MavenPublication::class.java) {
            groupId = project.group.toString()
            artifactId = project.base.archivesName.get()
            version = project.version.toString()
            from(components["java"])
            pom {
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://raw.githubusercontent.com/turtton/Weaver/main/LICENSE")
                    }
                }
            }
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
        val targetPath = System.getenv("PUBLISH_PATH")
        if (targetPath != null) {
            maven {
                url = uri(targetPath)
            }
        }
    }
}
