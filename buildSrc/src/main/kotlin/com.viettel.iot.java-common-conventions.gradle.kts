plugins {
    java
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    // constraints {
    //     implementation("org.apache.commons:commons-text:1.9")
    // }
    dependencies {
        "org.projectlombok:lombok:1.18.26".let {
            compileOnly(it)
            annotationProcessor(it)
            testCompileOnly(it)
            testAnnotationProcessor(it)
        }
    }
    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
