group = "org.uv"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:dataframe:0.13.1")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.1.2")
}

plugins {
    kotlin("jvm") version "1.9.23"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.jetbrains.kotlinx.dataframe") version "0.13.1"
}

javafx {
    val modulesList = ArrayList<String>()
    modulesList.add("javafx.controls")
    modulesList.add("javafx.fxml")

    version = "22.0.1"
    modules = modulesList
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}