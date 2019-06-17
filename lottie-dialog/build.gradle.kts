group = "com.github.AchmadHafid"

apply(from = "../shared-build.gradle")

android {
    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependsOn(
    Dependency.KOTLIN_STDLIB,

    Dependency.MATERIAL,

    Dependency.JETPACK_CORE,
    Dependency.JETPACK_APP_COMPAT,
    Dependency.JETPACK_CONSTRAINT_LAYOUT,

    Dependency.EXTRA_LOTTIE,

    Dependency.TESTING_CORE,
    Dependency.TESTING_ESPRESSO,
    Dependency.TESTING_EXT_JUNIT,
    Dependency.TESTING_EXT_TRUTH,
    Dependency.TESTING_ROBOLECTRIC
)
