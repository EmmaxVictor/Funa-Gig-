plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

// Ensure Kotlin uses JDK 17 for KSP and compilation
kotlin {
    jvmToolchain(17)
}

android {
    namespace = "application.com.funagig"
    compileSdk = 36

    defaultConfig {
        applicationId = "application.com.funagig"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = "1.1"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.tasks)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.common)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.swiperefreshlayout)


    // Room database - Java annotation processing is sufficient for our all-Java DAOs/entities
    implementation(libs.room.runtime)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    annotationProcessor(libs.room.compiler)
    
    // Local Broadcast Manager for internal broadcasts
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
        // Import the BoM for the Firebase platform
        implementation (platform("com.google.firebase:firebase-bom:34.5.0"))

        // Declare the dependencies for the desired Firebase products without specifying versions
        // For example, declare the dependencies for Firebase Authentication and Cloud Firestore
        //implementation ("com.google.firebase:firebase-auth")
        implementation ("com.google.firebase:firebase-firestore")

}