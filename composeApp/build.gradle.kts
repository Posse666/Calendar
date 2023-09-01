@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeAsciiOnly
import java.util.Properties

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.cocoapods)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.complete.kotlin)
    alias(libs.plugins.android.crashlytics)
    alias(libs.plugins.moko.resources)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()
    android {//TODO androidTarget after kotlin 1.9.0
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = "1.0.0"
        summary = "Compose application framework"
        homepage = "empty"
        ios.deploymentTarget = "14.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
        val commonMain by getting {
            dependencies {
                api(compose.foundation)
                api(compose.animation)
                api(libs.precompose)
                api(libs.precompose.viewmodel)
                implementation(compose.runtime)
                implementation(compose.material3)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.multiplatformSettings)
                implementation(libs.multiplatformSettings.serialization)
                implementation(libs.immutableCollections)
                implementation(libs.moko.permissions)
                implementation(libs.moko.permissions.compose)
                implementation(libs.moko.resources.core)
                implementation(libs.moko.resources.compose)
                implementation(libs.moko.resources.graphics)
                implementation(libs.kodein.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.activityCompose)
                implementation(libs.kotlinx.coroutines.android)
                implementation(platform(libs.firebase.bom.get()))
                implementation(libs.firebase.analytics.ktx)
                implementation(libs.firebase.crashlytics.ktx)
                implementation(libs.android.systemUiController)
            }
        }

        val iosMain by getting {
            dependencies {
            }
        }

    }
}

android {
    namespace = "com.posse.kotlin1.calendar"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 34

        applicationId = "com.posse.kotlin1.calendar"
        versionCode = 7
        versionName = "2.0"
    }

    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/resources")
        resources.srcDirs("src/commonMain/resources")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            val properties = Properties().apply {
                load(File(projectDir, "signingKey.properties").reader())
            }

            storeFile = File(properties.getProperty("path_to_store_file"))
            storePassword = properties.getProperty("store_password")
            keyAlias = properties.getProperty("key_alias")
            keyPassword = properties.getProperty("key_password")

            tasks.withType<KotlinCompile>().all {
                kotlinOptions {
                    freeCompilerArgs = freeCompilerArgs.plus(
                        listOf(
                            "-Xno-call-assertions",
                            "-Xno-receiver-assertions",
                            "-Xno-param-assertions"
                        )
                    )
                }
            }
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "TYPE", "\"RELEASE\"")
        }

        getByName("debug") {
            signingConfig = signingConfigs.getByName("release")
            versionNameSuffix = "-debug"
//            applicationIdSuffix = ".debug"
            buildConfigField("String", "TYPE", "\"DEBUG\"")
        }
    }

    applicationVariants.all {
        outputs.forEach { output ->
            val bundleFinalizeTaskName = StringBuilder("sign").run {
                productFlavors.forEach {
                    append(it.name.capitalizeAsciiOnly())
                }
                append(buildType.name.capitalizeAsciiOnly())
                append("Bundle")
                toString()
            }

            val outputName = buildString {
                append("app-")
                productFlavors.forEach {
                    append(it.name)
                    append("-")
                }
                append("v$versionName")
            }

            tasks.named(
                bundleFinalizeTaskName,
                com.android.build.gradle.internal.tasks.FinalizeBundleTask::class.java
            ) {
                val file = finalBundleFile.asFile.get()
                val finalFile = File(file.parentFile, "$outputName.aab")
                finalBundleFile.set(finalFile)
            }
            if (output is com.android.build.gradle.internal.api.BaseVariantOutputImpl) {
                output.outputFileName = "$outputName.${output.outputFile.extension}"
            }
        }
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "resources"
}