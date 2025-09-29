import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    id("java-library")
    alias(libs.plugins.checkerframework)
    alias(libs.plugins.publish)
}

group = "org.allaymc"
version = "3.0.10"

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        configureEach {
            options.isFork = true
        }
    }

    // We already have sources jar, so no need to build Javadoc, which would cause a lot of warnings
    withType<Javadoc> {
        enabled = false
    }

    withType<Jar> {
        manifest {
            attributes("Automatic-Module-Name" to "org.cloudburstmc.nbt")
        }
    }

    withType<Copy> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    withType<Test> {
        useJUnitPlatform()
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    checkerFramework(libs.checker)
    compileOnly(libs.checker.qual)
    testCompileOnly(libs.checker.qual)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    testImplementation(libs.jmh.core)
    testAnnotationProcessor(libs.jmh.generator.annprocess)
}

configure<MavenPublishBaseExtension> {
    publishToMavenCentral()
    signAllPublications()

    coordinates(project.group.toString(), project.name, project.version.toString())

    pom {
        name.set(project.name)
        description.set("A library for reading and writing NBT data.")
        inceptionYear.set("2023")
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