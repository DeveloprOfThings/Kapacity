plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.androidLint) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
}

val projectGroup = project.findProperty("GROUP")?.toString() ?: "io.github.developrofthings"

val kapacityLibVersion = libs.versions.kapacity.get()

// Explicitly apply to your modules
project(":kapacity") {
    configurePublishing(
        groupId = projectGroup,
        artifactId = this.name,
        versionName = kapacityLibVersion,
        pomName = "Kapacity Core",
        pomDescription = "A lightweight, zero-allocation Kotlin Multiplatform library designed to make handling data sizes safe and intuitive."
    )
}

project(":ktxIO") {
    configurePublishing(
        groupId = projectGroup,
        artifactId = this.name,
        versionName = kapacityLibVersion,
        pomName = "Kapacity kotlinx-io Extensions",
        pomDescription = "Seamless extensions for integrating Kapacity with kotlinx-io buffers and byte strings."
    )
}

// A helper function to apply the publishing logic
fun Project.configurePublishing(
    groupId: String,
    artifactId: String,
    versionName: String,
    pomName: String,
    pomDescription: String,
) {
    apply(plugin = "com.vanniktech.maven.publish")

    extensions.configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {

        publishToMavenCentral()

        signAllPublications()

        coordinates(
            groupId = groupId,
            artifactId = artifactId,
            version = versionName
        )

        pom {
            name.set(pomName)
            description.set(pomDescription)
            inceptionYear = "2026"
            url = "https://github.com/DeveloprOfThings/Kapacity"
            licenses {
                license {
                    name = "MIT"
                    url = "https://opensource.org/license/MIT"
                    distribution = "https://opensource.org/license/MIT"
                }
            }
            developers {
                developer {
                    id = "DeveloprOfThings"
                    name = "Developr Of Things"
                    url = "https://github.com/DeveloprOfThings"
                }
            }
            scm {
                url = "https://github.com/DeveloprOfThings/Kapacity"
                connection = "scm:git:https://github.com/DeveloprOfThings/Kapacity.git"
                developerConnection = "scm:git:https://github.com/DeveloprOfThings/Kapacity.git"
            }
        }
    }
}