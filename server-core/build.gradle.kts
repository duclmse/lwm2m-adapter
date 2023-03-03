plugins {
    id("com.viettel.iot.java-library-conventions")
}

val leshan = mapOf("version" to "2.0.0-M10")

dependencies {
    implementation(project(":core"))

    implementation("org.eclipse.leshan:leshan-server-cf:${leshan["version"]}")

    implementation("info.picocli:picocli:4.7.1")
    implementation("info.picocli:picocli-shell-jline2:4.7.1")
    implementation("org.eclipse.jetty:jetty-webapp:11.0.14")
    implementation("org.eclipse.jetty:jetty-servlets:11.0.14")
    implementation("org.apache.commons:commons-lang3:3.12.0")
}
