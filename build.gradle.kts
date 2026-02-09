plugins {
    kotlin("jvm") version "2.2.20"
    id("io.qameta.allure-report") version "3.0.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("io.qameta.allure:allure-junit5")
    testImplementation(platform("io.qameta.allure:allure-bom:2.25.0"))
    implementation("io.rest-assured:rest-assured:6.0.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
    outputs.upToDateWhen { false }
    ignoreFailures = true

    environment("JB_API_KEY", System.getenv("JB_API_KEY"))
    environment("JB_API_KEY_ORG_VIEWER", System.getenv("JB_API_KEY_ORG_VIEWER"))
    environment("JB_API_KEY_TEAM_AB_ADMIN", System.getenv("JB_API_KEY_TEAM_AB_ADMIN"))
    environment("JB_API_KEY_TEAM_B_ADMIN", System.getenv("JB_API_KEY_TEAM_B_ADMIN"))
    environment("JB_API_KEY_TEAM_B_VIEWER", System.getenv("JB_API_KEY_TEAM_B_VIEWER"))
    environment("JB_CUSTOMER_CODE", System.getenv("JB_CUSTOMER_CODE"))

}