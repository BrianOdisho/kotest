import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")
   id("maven-publish")
   id("com.adarshr.test-logger")
}

repositories {
   mavenCentral()
}

kotlin {

   targets {
      jvm {
         compilations.all {
            kotlinOptions {
               jvmTarget = "1.8"
            }
         }
      }
   }

   targets.all {
      compilations.all {
         kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + "-Xuse-experimental=kotlin.Experimental"
         }
      }
   }

   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(kotlin("stdlib-jdk8"))
            implementation(kotlin("reflect"))
            api(project(Projects.Mpp))
            api(project(Projects.AssertionsShared))
            api(project(Projects.Core))
            api(project(Projects.Extensions))
            api("junit:junit:4.13")
            api(Libs.Coroutines.core)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.AssertionsCore))
            implementation(Libs.JUnitPlatform.testkit)
         }
      }
   }
}

tasks.named<Test>("jvmTest") {
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(TestLogEvent.FAILED, TestLogEvent.PASSED)
      exceptionFormat = TestExceptionFormat.FULL
   }
}

apply(from = "../../publish-mpp.gradle.kts")

