plugins {
    id("java-gradle-plugin")
    kotlin("jvm")
    id("com.gradle.plugin-publish")
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
}

pluginBundle {
    website = "https://github.com/milis92/Krang"
    vcsUrl = "https://github.com/milis92/Krang.git"
    tags = listOf("kotlin", "krang", "function-interceptor", "debugLog")
}

gradlePlugin {
    plugins {
        create("krang") {
            id = "com.github.milis92.krang"
            displayName = "Kotlin function logging interceptor"
            description = "Kotlin Compiler Plugin which adds logging interceptors to the functions"
            implementationClass = "com.herman.krang.KrangGradlePlugin"
        }
    }
}

tasks.register("publish") {
    dependsOn("publishPlugins")
}