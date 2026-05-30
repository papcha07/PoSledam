// Top-level build file where you can add configuration options common to all sub-projects/modules.
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("com.google.gms.google-services") version "4.4.4" apply false
    alias(libs.plugins.android.library) apply false
    jacoco
}

ktlint {
    android.set(true)
    ignoreFailures.set(false)
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.register<JacocoReport>("featureUnitTestCoverage") {
    group = "verification"
    description = "Generates Jacoco coverage for feature ViewModels and repositories."
    dependsOn(":app:testDebugUnitTest")

    val featureModules = listOf(
        "auth",
        "main",
        "profile",
        "search",
        "core_datastore"
    )
    val classIncludes = listOf(
        "**/*ViewModel.class",
        "**/*RepositoryImpl.class"
    )

    executionData.setFrom(
        layout.projectDirectory.file("app/build/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    )
    classDirectories.setFrom(
        featureModules.map { moduleName ->
            fileTree(layout.projectDirectory.dir("$moduleName/build/tmp/kotlin-classes/debug")) {
                include(classIncludes)
            }
        }
    )
    sourceDirectories.setFrom(
        featureModules.map { moduleName ->
            layout.projectDirectory.dir("$moduleName/src/main/kotlin")
        }
    )

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(true)
    }
}
