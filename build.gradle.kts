plugins {
    kotlin("jvm") version "2.1.20"
    id("io.ktor.plugin") version "2.3.12"
    kotlin("plugin.serialization") version "2.0.21"
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    implementation("org.jetbrains.exposed:exposed-core:0.56.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.56.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.56.0")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    implementation("at.favre.lib:bcrypt:0.9.0")

    implementation("io.insert-koin:koin-ktor:3.5.0")
    implementation("io.insert-koin:koin-logger-slf4j:3.5.0")




}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}