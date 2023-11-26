    plugins {
        id("com.android.application")
        id("org.jetbrains.kotlin.android")
        id("dagger.hilt.android.plugin")
        id("kotlin-kapt")
    }

    android {
        namespace = "com.mmusic.player"
        compileSdk = 34

        defaultConfig {
            applicationId = "com.mmusic.player"
            minSdk = 21
            targetSdk = 33
            versionCode = 1
            versionName = "1.0"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables {
                useSupportLibrary = true
            }
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
            compose = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.3"
        }
        packaging {
            resources {
             //   excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }

        buildFeatures {
            viewBinding = true
        }
    }

    dependencies {

        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
        implementation("androidx.activity:activity-compose:1.8.0")
        implementation(platform("androidx.compose:compose-bom:2023.03.00"))
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.ui:ui-graphics")
        implementation("androidx.compose.ui:ui-tooling-preview")
        debugImplementation("androidx.compose.ui:ui-tooling")
        debugImplementation("androidx.compose.ui:ui-test-manifest")
        implementation("androidx.compose.ui:ui-tooling:1.6.0-alpha08")

        implementation("androidx.compose.material3:material3:1.2.0-alpha10")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.10.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

        androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
        androidTestImplementation("androidx.compose.ui:ui-test-junit4")
        debugImplementation("androidx.compose.ui:ui-tooling")
        debugImplementation("androidx.compose.ui:ui-test-manifest")

        // Glide
        implementation("com.github.bumptech.glide:compose:1.0.0-beta01")


        // Hilt version
        val hiltVersion = "2.48.1"
        val navigationVersion = "1.1.0-rc01"
        implementation("com.google.dagger:hilt-android:$hiltVersion")
        kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
        implementation("androidx.hilt:hilt-common:$navigationVersion")
        kapt("androidx.hilt:hilt-compiler:$navigationVersion")
        implementation("androidx.hilt:hilt-navigation-fragment:$navigationVersion")
        implementation("androidx.hilt:hilt-navigation-compose:$navigationVersion")
        implementation("androidx.navigation:navigation-compose:2.7.4")


        implementation("io.coil-kt:coil-compose:2.4.0")
        implementation("com.github.Kaaveh:sdp-compose:1.1.0")
        runtimeOnly("androidx.compose.material:material-icons-extended:1.6.0-alpha08")

        implementation("androidx.media3:media3-exoplayer:1.1.1")
        implementation("androidx.media3:media3-session:1.1.1")
        implementation("androidx.media3:media3-ui:1.1.1")
        implementation("androidx.media:media:1.6.0")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.7.3")


    }