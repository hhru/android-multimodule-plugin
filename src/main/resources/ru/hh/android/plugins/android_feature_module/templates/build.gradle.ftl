apply plugin: 'com.android.library'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-kapt'
<#if need_add_ui_modules_dependencies>
apply plugin: 'kotlin-android-extensions'
</#if>


android {
    compileSdkVersion rootProject.ext.targetSdkVersion

    defaultConfig {
        minSdkVersion rootProject.ext.minSdkVersion
        targetSdkVersion rootProject.ext.targetSdkVersion
        versionCode 1
        versionName "1.0"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [ toothpick_registry_package_name : '${package_name}' ]
            }
        }

        <#if need_add_ui_modules_dependencies>
        vectorDrawables.useSupportLibrary = true
        </#if>
    }

    <#if enable_moxy>
    kapt {
        arguments {
            arg("moxyReflectorPackage", '${package_name}')
        }
    }
    </#if>

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

}

dependencies {
    def libraries = rootProject.ext.deps

    <#list libraries_modules as module>
    compileOnly project(':${module}')
    </#list>

    // Kotlin
    implementation libraries.kotlin

    // DI
    compileOnly libraries.toothpick
    kapt libraries.toothpickCompiler

    <#if enable_moxy>
    // Moxy
    compileOnly libraries.moxy
    compileOnly libraries.moxyAppCompat
    kapt libraries.moxyCompiler
    </#if>

    <#if need_create_api_interface>
    // Gson
    compileOnly libraries.gson

    // Network
    compileOnly libraries.retrofit

    // RxJava
    compileOnly libraries.rxJava
    compileOnly libraries.rxAndroid
    </#if>
}