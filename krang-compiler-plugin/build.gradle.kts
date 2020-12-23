plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("signing")
    id("maven-publish")
    id("org.jetbrains.dokka")
}

dependencies {
    compileOnly(project(":krang-runtime"))
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    kapt("com.google.auto.service:auto-service:1.0-rc6")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0-rc6")


    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(kotlin("test-junit"))

    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.2.6")
    testImplementation(project(":krang-runtime"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("sourcesJar", Jar::class) {
    group = "build"

    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
    dependsOn(JavaPlugin.CLASSES_TASK_NAME)
}

tasks.register("dokkaJar", Jar::class) {
    group = "documentation"

    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
    dependsOn(tasks.dokkaJavadoc)
}

signing {
    setRequired(provider { gradle.taskGraph.hasTask("publishPlugins") })
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

    //TODO Check publishing configuration
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
