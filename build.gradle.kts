subprojects {
    if (name == "common" || name.startsWith("module-")) {
        group = "com.posty"
        version = "0.0.1-SNAPSHOT"

        pluginManager.withPlugin("java") {
            repositories {
                mavenCentral()
            }

            configure<JavaPluginExtension> {
                toolchain {
                    languageVersion.set(JavaLanguageVersion.of(21))
                }
            }

            dependencies {
                "implementation"("org.springframework.boot:spring-boot-starter-data-jpa")
                "runtimeOnly"("com.mysql:mysql-connector-j")
                "implementation"("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.2")

                "testImplementation"("org.springframework.boot:spring-boot-starter-test")
                "testImplementation"("org.springframework.boot:spring-boot-testcontainers")
                "testImplementation"("org.testcontainers:mysql:1.20.4")
                "testImplementation"("org.testcontainers:junit-jupiter:1.20.4")
                "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
            }

            tasks.withType<Test> {
                useJUnitPlatform()
            }
        }
    }
}
