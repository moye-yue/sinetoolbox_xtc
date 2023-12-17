
plugins {
    id("com.android.application")
}

android {
    namespace = "moye.sinetoolbox.xtc"
    compileSdk = 33

    defaultConfig {
        applicationId = "moye.sinetoolbox.xtc"
        minSdk = 19
        targetSdk = 25
        versionCode = 37
        versionName = "XTC3.6"
        resConfigs("zh")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    packagingOptions {
        exclude("META-INF/LICENSE")
        exclude("META-INF/DEPENDENCIES")
    }
}
dependencies {
    implementation(files("libs/mina-core-2.1.6.jar"))
    implementation(files("libs/slf4j-api-1.7.36.jar"))
    implementation(files("libs/slf4j-reload4j-1.7.36.jar"))
    implementation(files("libs/log4j-1.2.14.jar"))
    implementation(files("libs/ftplet-api-1.1.4.jar"))
    implementation(files("libs/ftpserver-core-1.1.4.jar"))
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.jetbrains:annotations:15.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}