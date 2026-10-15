import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  flavorDimensions += "store"

  productFlavors {
    create("bazaar") {
      dimension = "store"
      buildConfigField("String", "STORE_FLAVOR", "\"bazaar\"")
    }

    create("myket") {
      dimension = "store"
      buildConfigField("String", "STORE_FLAVOR", "\"myket\"")
      manifestPlaceholders["marketApplicationId"] = "ir.mservices.market"
      manifestPlaceholders["marketBindAddress"] = "ir.mservices.market.InAppBillingService.BIND"
      manifestPlaceholders["marketPermission"] = "ir.mservices.market.BILLING"
    }
  }

  defaultConfig {
    applicationId = "com.aistudio.freelancepro.vzxkp"
    minSdk = 24
    targetSdk = 36
    // Increment for the next store submission so it can update the installed release.
    versionCode = 2
    versionName = "1.1"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    // دریافت کلیدهای عمومی از GitHub Actions Secrets
    val bazaarRsaKey =
      System.getenv("BAZAAR_RSA_PUBLIC_KEY").orEmpty()

    val myketRsaKey =
      System.getenv("MYKET_RSA_PUBLIC_KEY").orEmpty()

    val tapsellAppKey =
      System.getenv("TAPSELL_APP_KEY").orEmpty()

    val geminiApiKey =
      System.getenv("GEMINI_API_KEY").orEmpty()

    buildConfigField(
      "String",
      "BAZAAR_RSA_PUBLIC_KEY",
      "\"$bazaarRsaKey\""
    )

    buildConfigField(
      "String",
      "MYKET_RSA_PUBLIC_KEY",
      "\"$myketRsaKey\""
    )

    buildConfigField(
      "String",
      "TAPSELL_APP_KEY",
      "\"$tapsellAppKey\""
    )

    buildConfigField(
      "String",
      "GEMINI_API_KEY",
      "\"$geminiApiKey\""
    )
  }

  // تنظیمات امضای برنامه؛ حفظ شده‌اند.
  signingConfigs {
    create("release") {
      val keystorePath =
        System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"

      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }

    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false

      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )

      signingConfig = signingConfigs.getByName("release")
    }

    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }

  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// تنظیمات Secrets Gradle Plugin
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

googleServices {
  missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN
}

dependencies {
  // پرداخت کافه‌بازار فقط برای نسخهٔ Bazaar
  add(
    "bazaarImplementation",
    "com.github.cafebazaar.Poolakey:poolakey:2.2.0"
  )

  // Official Myket billing client, isolated to the Myket flavor.
  add(
    "myketImplementation",
    "com.github.myketstore:myket-billing-client:1.19"
  )

  implementation("ir.tapsell.plus:tapsell-plus-sdk-android:2.3.3")

  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))

  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.firebase.appcheck.debug)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  implementation(libs.retrofit)

  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)

  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)

  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)

  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}
