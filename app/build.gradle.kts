
plugins {
    id("com.android.application")
}

android {
    namespace = "com.moneyprinter.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.moneyprinter.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.1"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // HTTP requests to the MoneyPrinter Render API
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}
