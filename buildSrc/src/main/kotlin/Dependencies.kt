object Dependencies {
    object Android {
        const val coreKtx = "androidx.core:core-ktx:1.5.0"
        const val appCompat = "androidx.appcompat:appcompat:1.3.0"
        const val material = "com.google.android.material:material:1.3.0"

        const val composeUI = "androidx.compose.ui:ui:${Version.composeLib}"
        const val composeUITooling = "androidx.compose.ui:ui-tooling:${Version.composeLib}"
        const val composeMaterial = "androidx.compose.material:material:${Version.composeLib}"
        const val composeActivity = "androidx.activity:activity-compose:1.5.0"
        const val navigation = "androidx.navigation:navigation-compose:2.5.0"

        const val lifecycleKts = "androidx.lifecycle:lifecycle-runtime-ktx:2.3.1"
    }

    object Koin {
        const val android = "io.insert-koin:koin-android:${Version.koin}"
    }

    object Firebase {
        const val bom = "com.google.firebase:firebase-bom:28.0.1"
        const val firestore = "com.google.firebase:firebase-firestore"
    }

    object Test {
        const val jUnit4 = "junit:junit:4.+"
        const val mockk = "io.mockk:mockk:${Version.mockk}"
    }

    object Coroutines{
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.coroutines}"
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.coroutines}"
    }
}

object Version {
    const val kotlin = "1.7.0"
    const val jUnit5 = "5.3.1"
    const val compose = "1.2.0"
    const val composeLib = "1.2.0-rc03"
    const val koin = "3.0.2"
    const val mockk = "1.12.4"
    const val coroutines = "1.6.4"

    object Android {
        const val buildTool = "30.0.3"
        const val minSdk = 23
        const val targetSdk = 32
        const val compileSdk = 32
    }
}
