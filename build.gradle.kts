plugins {
    id("java")
    id("com.github.johnrengelman.shadow")
    application
}

group = "org.bomb"
version = "1.0"

application { mainClass.set("org.bomb.BombGame") }

java {  toolchain.languageVersion.set(JavaLanguageVersion.of(21)) }

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    shadowJar {
        archiveFileName.set("bombermon.jar")
        isZip64 = true
        dependsOn(distTar, distZip)
    }
}