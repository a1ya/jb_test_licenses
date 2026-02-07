plugins {
    kotlin("jvm") version "2.2.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("io.rest-assured:rest-assured:6.0.0")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(24)
}