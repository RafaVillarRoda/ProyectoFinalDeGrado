

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)

}

android {
    namespace = "com.example.proyectofinaldegrado"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.proyectofinaldegrado"
        minSdk = 24
        targetSdk = 36
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

    buildFeatures {
        compose = true
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.ktx)
    
    // Dependencias de Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.foundation)
    ksp(libs.androidx.room.compiler)


    implementation(libs.androidx.compose.material3)


    // Integración de Compose con Activity
    implementation(libs.androidx.activity.compose)
    implementation("androidx.compose.material:material-icons-extended-android:1.7.8")


    // Bill of Materials (BOM) para gestionar las versiones de Compose

    implementation(platform(libs.androidx.compose.bom))

    // Librerías de UI de Compose
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview) // Para las vistas previas en el IDE
    implementation(libs.androidx.material3) // Para Material Design 3 (incluye el carrusel)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Libreria para manejo de fechas
    implementation(libs.kotlinx.datetime)


        // ...
    implementation(libs.gson)



}
