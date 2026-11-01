plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(platform(
        "org.springframework.boot:spring-boot-dependencies:4.1.2"
    ))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation(kotlin("reflect"))
    implementation(project(":economic-model"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}