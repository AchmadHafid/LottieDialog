apply(from = "../shared-build.gradle")

android {
    defaultConfig {
        applicationId = "io.github.achmadhafid.sample_app"
    }
}

dependsOn(
    Dependency.KOTLIN_STDLIB,

    Dependency.MATERIAL,

    Dependency.JETPACK_CORE,
    Dependency.JETPACK_APP_COMPAT,
    Dependency.JETPACK_ACTIVITY,
    Dependency.JETPACK_CONSTRAINT_LAYOUT,
    Dependency.JETPACK_CARD_VIEW,

    Dependency.TESTING_CORE,
    Dependency.TESTING_ESPRESSO,
    Dependency.TESTING_EXT_JUNIT,
    Dependency.TESTING_EXT_TRUTH,
    Dependency.TESTING_ROBOLECTRIC
)

dependsOn(":lottie-dialog")
