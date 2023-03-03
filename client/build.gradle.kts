plugins {
    id("com.viettel.iot.java-library-conventions")
}

val leshan = mapOf("version" to "2.0.0-M10")

dependencies {
    implementation(project(":core"))

    implementation("org.eclipse.leshan:leshan-client-cf:${leshan["version"]}")

    implementation("info.picocli:picocli:4.7.1")
    implementation("info.picocli:picocli-shell-jline2:4.7.1")
}
