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
}

tasks.withType<Test>().configureEach {
    System.getenv("JB_API_KEY")?.let { environment("JB_API_KEY", it) }
    System.getenv("JB_API_KEY_ORG_VIEWER")?.let { environment("JB_API_KEY_ORG_VIEWER", it) }
    System.getenv("JB_API_KEY_TEAM_AB_ADMIN")?.let { environment("JB_API_KEY_TEAM_AB_ADMIN", it) }
    System.getenv("JB_API_KEY_TEAM_B_ADMIN")?.let { environment("JB_API_KEY_TEAM_B_ADMIN", it) }
    System.getenv("JB_API_KEY_TEAM_B_VIEWER")?.let { environment("JB_API_KEY_TEAM_B_VIEWER", it) }
    System.getenv("JB_CUSTOMER_CODE")?.let {environment("JB_CUSTOMER_CODE", it) }

}