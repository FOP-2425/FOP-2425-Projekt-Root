plugins {
    java
    application
    alias(libs.plugins.algomate)
    alias(libs.plugins.style)
    alias(libs.plugins.jagr.gradle)
    alias(libs.plugins.javafx)
}

version = file("version").readLines().first()

exercise {
    assignmentId.set("hProjekt")
}

submission {
    // ACHTUNG!
    // Setzen Sie im folgenden Bereich Ihre TU-ID (NICHT Ihre Matrikelnummer!), Ihren Nachnamen und Ihren Vornamen
    // in Anführungszeichen (z.B. "ab12cdef" für Ihre TU-ID) ein!
    // BEISPIEL:
    // studentId = "ab12cdef"
    // firstName = "sol_first"
    // lastName = "sol_last"
    studentId = "ab12cdef"
    firstName = "sol_first"
    lastName = "sol_last"

    // Optionally require own tests for mainBuildSubmission task. Default is false
    requireTests = false
}

jagr {
    graders {
        val graderPublic by getting {
            rubricProviderName.set("hProjekt.HProjekt_RubricProviderPublic")
        }
        val graderPrivate by creating {
            parent(graderPublic)
            graderName.set("FOP-2425-Projekt-Private")
            rubricProviderName.set("hProjekt.HProjekt_RubricProviderPrivate")
        }
    }
}

dependencies {
    implementation(libs.annotations)
    implementation(libs.algoutils.student)
    testImplementation(libs.junit.core)
}

application {
    mainClass.set("hProjekt.Main")
}

tasks {
    val runDir = File("build/run")
    withType<JavaExec> {
        doFirst {
            runDir.mkdirs()
        }
        workingDir = runDir
    }
    test {
        doFirst {
            runDir.mkdirs()
        }
        workingDir = runDir
        jvmArgs(
            "-Djava.awt.headless=true",
            "-Dtestfx.robot=glass",
            "-Dtestfx.headless=true",
            "-Dprism.order=sw",
            "-Dprism.lcdtext=false",
            "-Dprism.subpixeltext=false",
            "-Dglass.win.uiScale=100%",
            "-Dprism.text=t2k"
        )
        useJUnitPlatform()
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    javadoc {
        options.jFlags?.add("-Duser.language=en")
        options.optionFiles = mutableListOf(project.file("src/main/javadoc.options"))
    }
}

javafx {
    version = "23"
    modules("javafx.controls", "javafx.graphics", "javafx.base", "javafx.fxml", "javafx.swing", "javafx.media")
}
