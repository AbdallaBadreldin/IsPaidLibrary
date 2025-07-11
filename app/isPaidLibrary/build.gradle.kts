plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.fstech.ispaidlibrary"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")

    // GSON
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    //logging interceptor
    implementation ("com.squareup.okhttp3:logging-interceptor:5.1.0")
}

publishing {
    publications {

        publications {
            register<MavenPublication>("release") {
                groupId = "com.fstech"
                artifactId = "isPaidLibrary"
                version = "1.0"

                afterEvaluate {
                    from(components["release"])
                }
            }
        }

        create<MavenPublication>("mavenJava") {
            pom {
                name = "isPaidLibrary"
                description = "Library to control your android app remotly by changing your paid status online, Shutdown your app anytime!"
                url = "https://github.com/AbdallaBadreldin/isPaidLibrary"
                properties = mapOf(
                    "myProp" to "value",
                    "prop.with.dots" to "anotherValue"
                )
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "AbdallaBadreldin"
                        name = "Abdalla badr"
                        email = "abdalla.badr852@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:git://https://github.com/AbdallaBadreldin/isPaidLibrary"
                    developerConnection = "scm:git:ssh://https://github.com/AbdallaBadreldin/isPaidLibrary"
                    url = "https://github.com/AbdallaBadreldin/isPaidLibrary"
                }
            }
        }
    }
}