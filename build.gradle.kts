import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mainRepos()
    }
    dependencies {
        classpath(PluginClasspath.AGP())
        classpath(PluginClasspath.KOTLIN())
    }
}

plugins {
    with(Plugin.DEPENDENCY_CHECKER) {id(id) version version}
    with(Plugin.DETEKT) {id(id) version version}
    with(Plugin.ANDROID_MAVEN) {id(id) version version}
}

allprojects {
    repositories {
        mainRepos()
    }
}

detekt {
    toolVersion = Plugin.DETEKT.version
    input       = files("$projectDir")
    config      = files("$project.rootDir/detekt-config.yml")
    filters     = ".*test.*,.*/resources/.*,.*/tmp/.*"
    parallel    = true
}

tasks.named<Delete>("clean") {
    delete(buildDir)
}

subprojects {
    if (name =="app") {
        apply(plugin = Plugin.ANDROID_APP.id)
    } else if (name == "lottie-dialog") {
        apply(plugin = Plugin.ANDROID_LIB.id)
    }
    apply(plugin = Plugin.KOTLIN_ANDROID.id)
    apply(plugin = Plugin.KTX.id)
    apply(plugin = Plugin.KAPT.id)

    tasks.withType<KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "1.8"
            allWarningsAsErrors = true
        }
    }
}
