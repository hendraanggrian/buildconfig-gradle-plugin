import java.nio.file.Files

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    dokka
    `bintray-release`
}

group = RELEASE_GROUP
version = RELEASE_VERSION

sourceSets {
    get("main").java.srcDir("src")
    get("test").java.srcDir("tests/src")
}

gradlePlugin {
    (plugins) {
        register(RELEASE_ARTIFACT) {
            id = "$RELEASE_GROUP.buildconfig"
            implementationClass = "$id.BuildConfigPlugin"
        }
    }
}

val ktlint by configurations.registering

dependencies {
    implementation(kotlin("stdlib", VERSION_KOTLIN))
    implementation(hendraanggrian("javapoet-ktx", VERSION_JAVAPOETKTX))

    testImplementation(kotlin("test-junit", VERSION_KOTLIN))

    ktlint {
        invoke(ktlint())
    }
}

tasks {
    register("deploy") {
        mustRunAfter("build")
        doLast {
            val from = buildDir.resolve("libs").listFiles()!!.first()
            val to = rootDir.resolve("integration-tests/${from.name}")
            val toPath = to.toPath()
            if (to.exists()) {
                Files.delete(toPath)
            }
            Files.move(from.toPath(), toPath)
        }
    }

    val ktlint by registering(JavaExec::class) {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        inputs.dir("src")
        outputs.dir("src")
        description = "Check Kotlin code style."
        classpath(configurations["ktlint"])
        main = "com.pinterest.ktlint.Main"
        args("src/**/*.kt")
    }
    "check" {
        dependsOn(ktlint)
    }
    register<JavaExec>("ktlintFormat") {
        group = "formatting"
        inputs.dir("src")
        outputs.dir("src")
        description = "Fix Kotlin code style deviations."
        classpath(configurations["ktlint"])
        main = "com.pinterest.ktlint.Main"
        args("-F", "src/**/*.kt")
    }

    named<org.jetbrains.dokka.gradle.DokkaTask>("dokka") {
        outputDirectory = "$buildDir/docs"
        doFirst {
            file(outputDirectory).deleteRecursively()
            buildDir.resolve("gitPublish").deleteRecursively()
        }
    }
}

publishKotlinFix()
publish {
    bintrayUser = BINTRAY_USER
    bintrayKey = BINTRAY_KEY
    dryRun = false

    userOrg = RELEASE_USER
    groupId = RELEASE_GROUP
    artifactId = RELEASE_ARTIFACT
    publishVersion = RELEASE_VERSION
    desc = RELEASE_DESC
    website = RELEASE_WEB
}
