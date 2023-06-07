@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/IDEA-262280

plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.checkerframework)
}

group = "cn.allay"
description = "A library for reading and writing NBT data."

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
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

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("NBT")
                packaging = "jar"
                description.set("A library for reading and writing NBT data.")
                url.set("https://github.com/AllayMC/NBT")

                scm {
                    connection.set("scm:git:git://github.com/AllayMC/NBT.git")
                    developerConnection.set("scm:git:ssh:/github.com/AllayMC/NBT.git")
                    url.set("https://github.com/AllayMC/NBT")
                }

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        name.set("CloudburstMC Team")
                        organization.set("CloudburstMC")
                        organizationUrl.set("https://github.com/CloudburstMC")
                    }
                    developer {
                        name.set("AllayMC Team")
                        organization.set("AllayMC")
                        organizationUrl.set("https://github.com/AllayMC")
                    }
                }
            }
        }
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
    javadoc {
        isFailOnError = false
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