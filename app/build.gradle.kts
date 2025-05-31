plugins {
    alias(libs.plugins.android.application)
}

android {
    buildFeatures {
        viewBinding = true
    }
    namespace = "com.example.chicook"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.chicook"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.retrofit)  // Retrofit2
    implementation(libs.converter.gson)  // Gson converter for Retrofit
    implementation(libs.okhttp)  // OkHttp
    implementation(libs.logging.interceptor)  // OkHttp logging
    implementation(libs.picasso)  // Picasso
    implementation(libs.glide)  // Glide
    implementation(libs.swiperefreshlayout)  // SwipeRefreshLayout
}