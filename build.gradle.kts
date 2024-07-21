plugins {
    kotlin("jvm") version "1.9.21"
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.21"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.9.21"

}

group = "ru.home.project"
version = "1.0"

repositories {
    mavenCentral()
}
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.session:spring-session-core")
    implementation("org.projectlombok:lombok:1.18.26")
    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("org.telegram:telegrambots-spring-boot-starter:6.7.0")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation(project(":yandex-market"))

//    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.testcontainers:testcontainers:1.19.0")
    testImplementation("org.testcontainers:junit-jupiter:1.19.0")
    testImplementation("org.testcontainers:mysql:1.19.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation ("com.squareup.moshi:moshi-kotlin:1.15.1")
    implementation ("com.squareup.moshi:moshi-adapters:1.15.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}