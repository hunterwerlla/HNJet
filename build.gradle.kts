plugins {
    id("com.android.application") version "7.2.0-alpha06" apply false
    id("com.android.library") version "7.2.0-alpha06" apply false
    id("org.jetbrains.kotlin.android") version "1.5.31" apply false
}

tasks.register<Delete>("clean") {
    setDelete(rootProject.buildDir)
}
