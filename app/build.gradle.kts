plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.daggerHilt)
    id("kotlin-parcelize")
    alias(libs.plugins.safeArgsPlugin)
    id("jacoco")
}

android {
    namespace = "com.seth.pitstopparadise"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.seth.pitstopparadise"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = "1.2"

      //  testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "com.seth.pitstopparadise.HiltTestRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

// JaCoCo config
jacoco {
    toolVersion = "0.8.10"
}

tasks.withType<Test> {

    extensions.configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

// Custom Jacoco report task
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(file("${buildDir}/reports/jacoco/html"))
    }

    val fileFilter = listOf(
        // Android / Build system generated
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",

        // Hilt / Dagger generated
        "**/*_Hilt*.*",
        "**/*_MembersInjector.class",
        "**/Hilt_*.*",
        "**/Dagger*.*",
        "**/*_Factory.*",
        "**/*_Component*.*",

        // Room generated
        "**/*_Dao_Impl*.*",
        "**/*_Database_Impl*.*",

        // ViewBinding / DataBinding
        "**/databinding/**",
        "**/viewbinding/**",

        // Others (optional)
        "**/*_Impl*.*"
    )

    val javaClasses = fileTree("${buildDir}/intermediates/javac/debug") {
        exclude(fileFilter)
    }

    val kotlinClasses = fileTree("${buildDir}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    classDirectories.setFrom(javaClasses + kotlinClasses)
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(files("${buildDir}/jacoco/testDebugUnitTest.exec"))
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // --- Unit testing ---
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.arch.core.testing)

    // --- Instrumented tests ---
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // UI testing
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.contrib)
    androidTestImplementation(libs.espresso.intents)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.rules)

    // MockWebServer for testing Retrofit calls
    testImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")

    implementation(libs.retrofit2)
    implementation(libs.retrofit2.gson)
    implementation(libs.interceptor)
    implementation(libs.glide)
    implementation(libs.okhttp)
    implementation(libs.shimmer)
    implementation(libs.lottie)
    implementation(libs.datastore)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")

    // Room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    kapt(libs.androidx.hilt.compiler)
    testImplementation(kotlin("test"))

    // Hilt testing for androidTest
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.52")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.52")

}
