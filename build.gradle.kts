import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    `maven-publish`
    signing
}

group = "com.houxinlin"
version = "1.1.0"

repositories {
    mavenCentral()
    maven {
        url=uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }

}

dependencies {
    testImplementation(kotlin("test"))
    api ("org.ow2.asm:asm-tree:9.3")
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
    api(kotlin("reflect"))
    api("com.google.code.gson:gson:2.9.1")
    api("org.mybatis:mybatis:3.5.11")
    api("mysql:mysql-connector-java:8.0.30")

    api("ch.qos.logback:logback-classic:1.3.0")

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "mini-api"
            groupId="com.houxinlin"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }

            }

            pom {
                name.set("mini-api")
                description.set("simple web server")
                url.set("https://github.com/houxinlin/mini-api")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("mini-api")
                        name.set("houxinlin")
                        email.set("2606710413@qq.com")
                    }
                }
                scm {
                    connection.set("scm:git:github.com:houxinlin/mini-api.git")
                    developerConnection.set("scm:git:ssh://github.com:houxinlin/mini-api.git")
                    url.set("gthub.com:houxinlin/mini-api")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri(layout.buildDirectory.dir("repos/releases"))
            val snapshotsRepoUrl = uri(layout.buildDirectory.dir("repos/snapshots"))
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
//            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
//            credentials {
//                username =properties["mavenCentralUsername"].toString()
//                password =properties["mavenCentralPassword"].toString()
//            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
java {
    withJavadocJar()
    withSourcesJar()
}
