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
