plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("com.diffplug.spotless") version "5.8.1"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.github.princesslana:smalld:0.3.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.12.0")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    annotationProcessor("org.immutables:value:2.8.8")
    compileOnly("org.immutables:value:2.8.8")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClassName = "com.github.princesslana.totwentytwo.ToTwentyTwo"
}

spotless {
    java {
        googleJavaFormat("1.9")
        target("src/*/java/**/*.java")
    }
}
