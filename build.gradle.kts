import org.jetbrains.kotlin.daemon.md5Digest
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import org.jetbrains.kotlin.gradle.utils.NativeCompilerDownloader

plugins {
    kotlin("multiplatform") version "1.8.21"
}

group = "org.jetbrains.kotlin"
version = "1.0"

repositories {
    mavenCentral()
}

kotlin {
    targetHierarchy.default()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()

    macosX64()
    macosArm64()

    linuxX64()
    linuxArm64()
}

fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

val zipAndMD5CommonizerResults by tasks.creating(Zip::class.java) {
    dependsOn("commonizeNativeDistribution")
    val konanHome = NativeCompilerDownloader(project).compilerDirectory.absoluteFile
    val commonizedLibrariesHome = konanHome.resolve("klib/commonized/${NativeCompilerDownloader.DEFAULT_KONAN_VERSION}")

    val commonizedTargets = listOf(
        "(ios_arm64, ios_simulator_arm64, ios_x64)",
        "(ios_arm64, ios_simulator_arm64, ios_x64, linux_arm64, linux_x64, macos_arm64, macos_x64, watchos_arm64, watchos_simulator_arm64, watchos_x64)",
        "(ios_arm64, ios_simulator_arm64, ios_x64, macos_arm64, macos_x64, watchos_arm64, watchos_simulator_arm64, watchos_x64)",
        "(linux_arm64, linux_x64)",
        "(macos_arm64, macos_x64)",
        "(watchos_arm64, watchos_simulator_arm64, watchos_x64)"
    )
    from(commonizedLibrariesHome) {
        commonizedTargets.forEach { include("$it/**") }
    }

    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true

    archivesName.set("commonizedTargetsLocations")
    destinationDirectory.set(buildDir)

    doLast {
        val file = archiveFile.get().asFile
        val md5sum = file.md5Digest().toHex()
        println("$file\nMD5: $md5sum")
    }
}

