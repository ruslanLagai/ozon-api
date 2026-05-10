plugins {
    kotlin("jvm") version "2.1.20"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jetbrains.kotlin.plugin.spring") version "2.1.20"
    id("org.jetbrains.kotlin.plugin.jpa") version "2.1.20"

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
    implementation("org.apache.commons:commons-lang3")
    implementation("org.telegram:telegrambots-spring-boot-starter:6.7.0")
    implementation("com.mysql:mysql-connector-j:9.4.0")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.3")
    implementation(project(":yandex-market"))

//    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
    testImplementation("com.h2database:h2")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation ("com.squareup.moshi:moshi-kotlin:1.15.1")
    implementation ("com.squareup.moshi:moshi-adapters:1.15.1")
    testImplementation("org.wiremock.integrations:wiremock-spring-boot:4.0.9")
}

tasks.test {
    useJUnitPlatform()
}

project(":yandex-market") {
    tasks.withType<Test>().configureEach {
        filter {
            isFailOnNoMatchingTests = false
        }
    }
}

kotlin {
    jvmToolchain(21)
}
//
//springBoot {
//    mainClass.value("ru.home.project.ozonapi.OzonApiApplication")
//}