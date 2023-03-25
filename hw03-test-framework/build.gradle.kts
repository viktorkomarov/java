import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id ("java")
    id ("com.github.johnrengelman.shadow")
}


tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("testFramework")
        archiveVersion.set("0.1")
        archiveClassifier.set("")
    }

    build {
        dependsOn(shadowJar)
    }
}
