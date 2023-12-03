rootProject.name = "java"
include ("hw01-gradle")
include ("hw02-generics")
include ("hw03-test-framework")
include("hw04-gc")
include("hw05-proxy")
include("hw06-atm")
include("hw07-patterns")
include("hw08-serialization")
include("hw09-jdbc")
include("hw10-hibernate")
include("hw11-spring")


pluginManagement {
    val jgitver: String by settings
    val dependencyManagement: String by settings
    val springframeworkBoot: String by settings
    val johnrengelmanShadow: String by settings
    val jib: String by settings
    val protobufVer: String by settings

    plugins {
        id("fr.brouillard.oss.gradle.jgitver") version jgitver
        id("io.spring.dependency-management") version dependencyManagement
        id("org.springframework.boot") version springframeworkBoot
        id("com.github.johnrengelman.shadow") version johnrengelmanShadow
        id("com.google.cloud.tools.jib") version jib
        id("com.google.protobuf") version protobufVer
    }
}