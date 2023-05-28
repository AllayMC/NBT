@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/IDEA-262280

plugins {
    id("java-library")
    alias(libs.plugins.checkerframework)
}

group = "cn.allay"
description = "A library for reading and writing NBT data."

repositories {
    mavenCentral()
}

dependencies {
    checkerFramework(libs.checker)

    compileOnly(libs.checker.qual)

    testCompileOnly(libs.checker.qual)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.jmh.core)
    testAnnotationProcessor(libs.jmh.generator.annprocess)
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name();
    }
    test {
        useJUnitPlatform()
    }
    jar {
        manifest {
            attributes("Automatic-Module-Name" to "org.cloudburstmc.nbt")
        }
    }
}