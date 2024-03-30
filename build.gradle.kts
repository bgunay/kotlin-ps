import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.5"
	id("io.spring.dependency-management") version "1.1.3"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
}

group = "com.fashiondigital"
version = "0.0.1-SNAPSHOT"


java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

val ktor_version="2.3.7"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.apache.commons:commons-csv:1.10.0")
	implementation("org.apache.commons:commons-io:1.3.2")
	implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.0")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.4.0")

	implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
	implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-call-logging-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-host-common-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-status-pages-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
	implementation("io.ktor:ktor-client-core-jvm:$ktor_version")
	implementation("io.ktor:ktor-client-cio-jvm:$ktor_version")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
	implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.2")


}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.bootBuildImage {
	builder.set("paketobuildpacks/builder-jammy-base:latest")
}
