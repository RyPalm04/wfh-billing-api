plugins {
    java
    groovy
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.palmer"

val appVersionBase: String by project
val appVersionQualifier: String = run {
    val ref = System.getenv("GITHUB_REF_NAME") ?: ""
    when {
        ref.isEmpty() -> "LOCAL"
        ref == "master" -> ""
        ref.matches(Regex("v\\d+\\.\\d+\\.\\d+")) -> ""
        else -> Regex("v\\d+\\.\\d+\\.\\d+-(.+)").find(ref)?.groupValues?.get(1) ?: "SNAPSHOT"
    }
}
version = if (appVersionQualifier.isEmpty()) appVersionBase else "$appVersionBase-$appVersionQualifier"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

springBoot {
    buildInfo()
}

dependencies {
    implementation("com.stripe:stripe-java:32.2.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("net.sf.jasperreports:jasperreports:6.21.5")
    // JAXB — removed from the JDK after Java 8, but JasperReports still needs it
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")
    testImplementation("org.spockframework:spock-core:2.3-groovy-4.0")
    testImplementation("org.spockframework:spock-spring:2.3-groovy-4.0")
    testImplementation("org.apache.groovy:groovy:4.0.24")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
