@file:Suppress("TopLevelPropertyNaming")

import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

//region Versions

private const val VERSION_KOTLIN = "1.3.31"

private const val VERSION_MATERIAL = "1.1.0-alpha07"

private const val VERSION_JETPACK_CORE = "1.2.0-alpha02"
private const val VERSION_JETPACK_APP_COMPAT = "1.1.0-beta01"
private const val VERSION_JETPACK_ACTIVITY = "1.0.0-beta01"
private const val VERSION_JETPACK_CONSTRAINT_LAYOUT = "2.0.0-beta2"
private const val VERSION_JETPACK_CARD_VIEW = "1.0.0"

private const val VERSION_EXTRA_LOTTIE = "3.0.6"

private const val VERSION_TESTING_CORE = "1.2.0"
private const val VERSION_TESTING_ESPRESSO = "3.2.0"
private const val VERSION_TESTING_EXT_JUNIT = "1.1.1"
private const val VERSION_TESTING_EXT_TRUTH = "1.2.0"
private const val VERSION_TESTING_ROBOLECTRIC = "4.3"

private const val VERSION_KAPT_JETPACK_ANNOTATION = "1.1.0-rc01"

//endregion
//region Configurations

private val IMPLEMENTATION = hashSetOf("implementation")
private val TEST_IMPLEMENTATION = hashSetOf("testImplementation")
private val ANDROID_TEST_IMPLEMENTATION = hashSetOf("androidTestImplementation")
private val UNIFIED_TEST_IMPLEMENTATION = TEST_IMPLEMENTATION + ANDROID_TEST_IMPLEMENTATION
private val KAPT = hashSetOf("kapt")

//endregion

enum class Dependency(
    private val version: String,
    private vararg val configs: Pair<String, Set<String>>
) {
    //region Kotlin

    KOTLIN_STDLIB(
        VERSION_KOTLIN,
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8" to IMPLEMENTATION
    ),

    //endregion
    //region Core

    MATERIAL(
        VERSION_MATERIAL,
        "com.google.android.material:material" to IMPLEMENTATION
    ),

    //endregion
    //region Jetpack

    JETPACK_CORE(
        VERSION_JETPACK_CORE,
        "androidx.core:core-ktx" to IMPLEMENTATION
    ),
    JETPACK_ACTIVITY(
        VERSION_JETPACK_ACTIVITY,
        "androidx.activity:activity-ktx" to IMPLEMENTATION
    ),
    JETPACK_APP_COMPAT(
        VERSION_JETPACK_APP_COMPAT,
        "androidx.appcompat:appcompat" to IMPLEMENTATION
    ),
    JETPACK_CONSTRAINT_LAYOUT(
        VERSION_JETPACK_CONSTRAINT_LAYOUT,
        "androidx.constraintlayout:constraintlayout" to IMPLEMENTATION
    ),
    JETPACK_CARD_VIEW(
        VERSION_JETPACK_CARD_VIEW,
        "androidx.cardview:cardview" to IMPLEMENTATION
    ),

    //endregion
    //region Extra

    EXTRA_LOTTIE(
        VERSION_EXTRA_LOTTIE,
        "com.airbnb.android:lottie" to IMPLEMENTATION
    ),

    //endregion
    //region Testing

    TESTING_CORE(
        VERSION_TESTING_CORE,
        "androidx.test:core-ktx" to UNIFIED_TEST_IMPLEMENTATION,
        "androidx.test:runner" to UNIFIED_TEST_IMPLEMENTATION,
        "androidx.test:rules" to UNIFIED_TEST_IMPLEMENTATION
    ),
    TESTING_ESPRESSO(
        VERSION_TESTING_ESPRESSO,
        "androidx.test.espresso:espresso-contrib" to UNIFIED_TEST_IMPLEMENTATION,
        "androidx.test.espresso:espresso-intents" to UNIFIED_TEST_IMPLEMENTATION,
        "androidx.test.espresso:espresso-accessibility" to UNIFIED_TEST_IMPLEMENTATION,
        "androidx.test.espresso:espresso-remote" to UNIFIED_TEST_IMPLEMENTATION,
        "androidx.test.espresso.idling:idling-concurrent" to UNIFIED_TEST_IMPLEMENTATION,
        "androidx.test.espresso.idling:idling-net" to UNIFIED_TEST_IMPLEMENTATION
    ),
    TESTING_EXT_JUNIT(
        VERSION_TESTING_EXT_JUNIT,
        "androidx.test.ext:junit-ktx" to UNIFIED_TEST_IMPLEMENTATION
    ),
    TESTING_EXT_TRUTH(
        VERSION_TESTING_EXT_TRUTH,
        "androidx.test.ext:truth" to UNIFIED_TEST_IMPLEMENTATION
    ),
    TESTING_ROBOLECTRIC(
        VERSION_TESTING_ROBOLECTRIC,
        "org.robolectric:robolectric" to TEST_IMPLEMENTATION
    ),

    //endregion
    //region Annotation Processor

    KAPT_JETPACK_ANNOTATION(
        VERSION_KAPT_JETPACK_ANNOTATION,
        "androidx.annotation:annotation" to KAPT
    );

    //endregion

    operator fun invoke(scope: DependencyHandlerScope) {
        configs.forEach {
            it.second.forEach {config ->
                scope.add(config, "${it.first}:$version")
            }
        }
    }

}

fun Project.dependsOn(vararg deps: Dependency) {
    dependencies { deps.forEach { it(this) } }
}

fun Project.dependsOn(vararg projects: String, configs: Set<String> = IMPLEMENTATION) {
    dependencies {
        projects.forEach { path ->
            configs.forEach {config ->
                DependencyHandlerScope.of(dependencies)
                    .add(config, project(path))
            }
        }
    }
}
