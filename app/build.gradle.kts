plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.tracer)
    alias(libs.plugins.kotlin.serialization)
}

tracer {
    create("defaultConfig") {
        pluginToken = "edYkiAjuFObdxOunWDMIzzYXpaWNozQnvV8ThqIdVp81"
        appToken = "N6l6o3QG81hAdGgoHPjkPFw8PEGSkBGP89IaoSVCWL8"
    }
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("/home/lazyhat/Desktop/AndroidStudioProjects/Keys/appkeystore.jks")
            storePassword = "seva2004"
            keyAlias = "lazyhatdev"
            keyPassword = "lazyhat2004"
        }
    }
    namespace = "com.lazyhat.novsuapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lazyhat.novsuapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 13
        versionName = "0.2.0"

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
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.version.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.version.get())
    }
    kotlinOptions {
        jvmTarget = libs.versions.java.version.get()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compilerExtensionVersion.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/versions/9/previous-compilation-data.bin"
        }
    }
}

dependencies {
    //Kotlinx
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization)
    //Core
    implementation(libs.core.ktx)
    //Lifecycle
    implementation(libs.lifecycle.runtime.ktx)
    //Activity
    implementation(libs.activity.compose)
    //Boms
    implementation(platform(libs.compose.bom))
    //UI
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.navigation)
    //Material
    implementation(libs.material3)
    //DI
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    //Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.client.content.negotiation)
    //Data
    implementation(libs.datastore)
    //Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    //Tracer
    implementation(libs.tracer.crash.report)
}