plugins {
    `java-library`
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.dependency.management)
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}
