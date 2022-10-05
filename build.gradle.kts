import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
}

group = "com.hxl.server"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation ("org.ow2.asm:asm-tree:9.3")
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
    implementation(kotlin("reflect"))
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("org.mybatis:mybatis:3.5.11")
    implementation("mysql:mysql-connector-java:8.0.30")


}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.jar{
//    from(})
    configurations.runtimeClasspath.get().forEach {
        from(zipTree(it))
    }
    duplicatesStrategy=DuplicatesStrategy.EXCLUDE
}
