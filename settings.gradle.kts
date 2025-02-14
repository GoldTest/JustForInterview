rootProject.name = "JustForInterview"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    //声明查找位置 plugin
    repositories {
        gradlePluginPortal()
        maven("https://jitpack.io")
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    //声明可用 plugin 不是应用依赖 只是声明
    plugins {
    }
}


dependencyResolutionManagement {
    //三方依赖 libs
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        gradlePluginPortal()
        mavenCentral()
        maven("https://jogamp.org/deployment/maven") //require by webview
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":composeApp")
//include(":server")
include(":shared")