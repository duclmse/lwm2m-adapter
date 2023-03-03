plugins {
    id("com.viettel.iot.java-application-conventions")
}

val leshan = mapOf("version" to "2.0.0-M10")

dependencies {
    implementation(project(":core"))
    implementation(project(":server-core"))

    implementation("org.eclipse.leshan:leshan-server-cf:${leshan["version"]}")
    implementation("info.picocli:picocli:4.7.1")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("info.picocli:picocli-shell-jline2:4.7.1")
    implementation("org.eclipse.jetty:jetty-webapp:11.0.14")
    implementation("org.eclipse.jetty:jetty-servlets:11.0.14")
    implementation("ch.qos.logback:logback-classic:1.4.5")
}
