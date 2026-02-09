buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
//    dependencies {
//
//    }
}

plugins {
    id("com.android.application") version "8.12.3" apply false
    id("org.jetbrains.kotlin.android") version "2.3.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.0" apply false
}