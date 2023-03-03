plugins {
    id("com.viettel.iot.java-library-conventions")
}

val leshan = mapOf("version" to "2.0.0-M10")

dependencies {
    implementation(project(":core"))
    implementation(project(":server-core"))

    implementation("org.jmdns:jmdns:3.5.8")
    implementation("org.eclipse.leshan:leshan-server-cf:${leshan["version"]}")
    implementation("org.eclipse.leshan:leshan-server-redis:${leshan["version"]}")
    implementation("org.eclipse.californium:californium-core:3.8.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("info.picocli:picocli:4.7.1")
    implementation("info.picocli:picocli-shell-jline2:4.7.1")
    implementation("org.eclipse.jetty:jetty-webapp:11.0.14")
    implementation("org.eclipse.jetty:jetty-servlets:11.0.14")
    implementation("commons-io:commons-io:2.11.0")
    implementation("ch.qos.logback:logback-classic:1.4.5")
}
