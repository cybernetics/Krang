plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("org.jetbrains.dokka")
    id("signing")
    id("maven-publish")
}

dependencies {
    compileOnly(project(":krang-runtime"))
    compileOnly("org.jetbrains.kotlin:kotlin-compiler")
    kapt("com.google.auto.service:auto-service:1.0-rc6")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0-rc6")
}

tasks.named("compileKotlin") { dependsOn("syncSource") }

tasks.register<Sync>("syncSource") {
    from(project(":krang-compiler-plugin").sourceSets.main.get().allSource)
    into("src/main/kotlin")
    filter {
        when (it) {
            "import org.jetbrains.kotlin.com.intellij.mock.MockProject" -> "import com.intellij.mock.MockProject"
            else -> it
        }
    }
    exclude { it.file.name == "BuildConfig.kt" }
}

tasks.register("sourcesJar", Jar::class) {
    group = "build"
    description = "Assembles Kotlin sources"

    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
    dependsOn(tasks.classes)
}

tasks.register("dokkaJar", Jar::class) {
    group = "documentation"

    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
    dependsOn(tasks.dokkaJavadoc)
}

signing {
    setRequired(provider { gradle.taskGraph.hasTask("publish") })
    sign(publishing.publications)
}

publishing {
    publications {
        create<MavenPublication>("default") {

            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["dokkaJar"])

            pom {
                name.set(project.name)
                description.set("Kotlin Compiler Plugin which adds logging interceptors to the functions")
                url.set("https://github.com/milis92/Krang")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://github.com/milis92/Krang/blob/master/LICENSE.txt")
                    }
                }
                scm {
                    url.set("https://github.com/milis92/Krang")
                    connection.set("https://github.com/milis92/Krang.git")
                }
                developers {
                    developer {
                        name.set("Ivan Milisavljevic")
                        url.set("https://github.com/milis92")
                    }
                }
            }
        }
    }

    repositories {
        if (
            hasProperty("sonatypeUsername") &&
            hasProperty("sonatypePassword") &&
            hasProperty("sonatypeSnapshotUrl") &&
            hasProperty("sonatypeReleaseUrl")
        ) {
            maven {
                val url = when {
                    "SNAPSHOT" in version.toString() -> property("sonatypeSnapshotUrl")
                    else -> property("sonatypeReleaseUrl")
                } as String
                setUrl(url)
                credentials {
                    username = property("sonatypeUsername") as String
                    password = property("sonatypePassword") as String
                }
            }
        }
        maven {
            name = "test"
            setUrl("file://${rootProject.buildDir}/localMaven")
        }
    }
}
