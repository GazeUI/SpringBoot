plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile> {
    // Related issues:
    //   - Gradle: https://github.com/gradle/gradle/issues/2510
    //   - Eclipse: https://github.com/eclipse/buildship/issues/934
    options.compilerArgs.addAll(arrayOf("--release", "8"))
}

repositories {
    jcenter()
}

dependencies {
    implementation("org.springframework:spring-web:5.2.0.RELEASE")
}