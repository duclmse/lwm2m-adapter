plugins {
    id("com.viettel.iot.java-library-conventions")
}

val leshan = mapOf("version" to "2.0.0-M10")

dependencies {
    implementation("org.eclipse.leshan:leshan-core:${leshan["version"]}")

    implementation("info.picocli:picocli:4.7.1")
    implementation("info.picocli:picocli-shell-jline2:4.7.1")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("org.eclipse.californium:californium-core:3.8.0")
    implementation("org.eclipse.californium:scandium:3.8.0")

    testImplementation("junit:junit:4.13.2")
}
