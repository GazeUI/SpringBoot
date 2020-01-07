plugins {
    `java-library`
    id("org.springframework.boot") version "2.2.2.RELEASE"
}

// Allows to omit version numbers when declaring dependencies
apply(plugin = "io.spring.dependency-management")

repositories {
    jcenter()
}

dependencies {
    implementation("org.springframework:spring-web")
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-webmvc")
    
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    withType<JavaCompile> {
        // Related issues:
        //   - Gradle: https://github.com/gradle/gradle/issues/2510
        //   - Eclipse: https://github.com/eclipse/buildship/issues/934
        options.compilerArgs.addAll(arrayOf("--release", "8"))
    }
    
    // The bootJar task requires a main class, so we have to use the classic jar task to build the library.
    jar {
        enabled = true
    }
    
    test {
        useJUnitPlatform()
    }
}