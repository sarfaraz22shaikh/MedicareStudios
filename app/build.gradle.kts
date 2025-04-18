plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.developer.opdmanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.developer.opdmanager"
        minSdk = 24
        targetSdk = 35
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
    implementation ("com.google.firebase:firebase-firestore:25.1.1")
    implementation ("com.razorpay:checkout:1.6.40")

    // responsive UI using sdp
    implementation ("com.intuit.sdp:sdp-android:1.1.1")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
    implementation ("com.google.firebase:firebase-auth:22.3.1")

    // Firebase Realtime Database
    implementation ("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-analytics")

    // Add the dependency for the Realtime Database library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-database")
    implementation ("com.jakewharton.threetenabp:threetenabp:1.4.6")
    implementation("com.loopj.android:android-async-http:1.4.9")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.airbnb.android:lottie:6.5.2")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.activity:activity:1.10.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.google.firebase:firebase-auth:23.1.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.9.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")


}
