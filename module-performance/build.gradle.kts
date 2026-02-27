plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.dependency.management)
}

dependencies {
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
}
