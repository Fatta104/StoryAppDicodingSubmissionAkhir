plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.fattanaufal.storyapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.fattanaufal.storyapp"
        minSdk = 31
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"https://story-api.dicoding.dev/v1/\"")
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "17"

        testOptions {
            unitTests.isReturnDefaultValues = true
            animationsDisabled = true
        }
    }
    }

    dependencies {
        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.10.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("com.google.android.gms:play-services-maps:18.2.0")
        implementation("com.google.android.gms:play-services-location:21.0.1")
        implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")

        // lottie gif
        implementation("com.airbnb.android:lottie:6.1.0")

        // UI
        implementation("com.github.bumptech.glide:glide:4.16.0")
//    implementation("androidx.fragment:fragment-ktx:1.2.5")

        // api
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

        // room
        implementation("androidx.room:room-ktx:2.6.0")
        implementation("androidx.room:room-runtime:2.6.0")
        ksp("androidx.room:room-compiler:2.6.0")

        // paging 3 + mediator
        implementation("androidx.paging:paging-runtime-ktx:3.2.1")
        implementation("androidx.room:room-paging:2.6.0")

        // Testing
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
        androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1") //RecyclerViewActions
        implementation("androidx.test.espresso:espresso-idling-resource:3.5.1")

        androidTestImplementation("androidx.arch.core:core-testing:2.2.0") //InstantTaskExecutorRule
        androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1") //TestDispatcher

        testImplementation("androidx.arch.core:core-testing:2.2.0") // InstantTaskExecutorRule
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1") //TestDispatcher


        //mockito
        testImplementation("org.mockito:mockito-core:5.2.0")
        testImplementation("org.mockito:mockito-inline:5.2.0")

        // viewmodel and live data
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

        // data store
        implementation("androidx.datastore:datastore-preferences:1.0.0")
    }
