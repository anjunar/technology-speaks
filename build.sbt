import com.here.bom.Bom
import scala.sys.process._

lazy val jacksonDependencies = Bom.dependencies("com.fasterxml.jackson" % "jackson-bom" % "2.13.2.1")
lazy val wildFlyDependencies = Bom.dependencies("org.wildfly.bom" % "wildfly-ee" % "35.0.0.Final")

lazy val redeploy = taskKey[Unit]("Hot-deploys the WAR file into Wildfly")
lazy val assemble = taskKey[File]("Paketiert ohne vorher zu kompilieren")

lazy val root = (project in file("."))
  .aggregate(
    scalaUniverse2, jsonMapper2, system, domain, rest, application
  )
  .settings(wildFlyDependencies)
  .settings(jacksonDependencies)
  .settings(
    ThisBuild / scalaVersion := "3.6.4",
    ThisBuild / version := "0.1.0-SNAPSHOT",

    ThisBuild / dependencyOverrides ++= wildFlyDependencies.key.value,
    ThisBuild / dependencyOverrides ++= jacksonDependencies.key.value,

    ThisBuild / javacOptions ++= Seq("--release", "17"),
    ThisBuild / scalacOptions ++= Seq("-release", "17")
  )

lazy val scalaUniverse2 = (project in file("library/scala-universe2"))
  .settings(
    libraryDependencies ++= Seq(
      "com.google.guava" % "guava" % "33.0.0-jre",
      "org.scala-lang" % "scala-reflect" % "2.13.16",
      "com.typesafe.scala-logging" % "scala-logging_3" % "3.9.5"
    )
  )

lazy val jsonMapper2 = (project in file("library/json-mapper2"))
  .dependsOn(scalaUniverse2)
  .settings(
    libraryDependencies ++= Seq(
      "jakarta.validation" % "jakarta.validation-api" % "3.1.0",
      "jakarta.enterprise" % "jakarta.enterprise.cdi-api" % "4.1.0",
      "jakarta.ws.rs" % "jakarta.ws.rs-api" % "4.0.0",
      "com.fasterxml.jackson.core" % "jackson-annotations" % "2.16.1",
      "org.apache.commons" % "commons-lang3" % "3.14.0",
      "org.apache.commons" % "commons-text" % "1.12.0"
    )
  )

lazy val system = (project in file("system"))
  .dependsOn(jsonMapper2)
  .settings(
    libraryDependencies ++= Seq(
      "commons-io" % "commons-io" % "2.19.0",
      "org.apache.commons" % "commons-lang3" % "3.17.0",
      "org.apache.commons" % "commons-text" % "1.13.1",
      "jakarta.platform" % "jakarta.jakartaee-api" % "10.0.0",
      "org.jboss.resteasy" % "resteasy-client" % "6.2.12.Final",
      "com.google.guava" % "guava" % "33.0.0-jre",
      "net.bytebuddy" % "byte-buddy" % "1.17.5",
      "org.slf4j" % "slf4j-api" % "2.0.17",
      "org.jsoup" % "jsoup" % "1.19.1",
      "org.thymeleaf" % "thymeleaf" % "2.0.21",
      "org.thymeleaf.extras" % "thymeleaf-extras-java8time" % "3.0.4.RELEASE",
      "com.yubico" % "webauthn-server-core" % "2.6.0",
      "org.jboss.resteasy" % "resteasy-jackson2-provider" % "6.2.12.Final"
    )
  )


lazy val domain = (project in file("domain"))
  .dependsOn(system)
  .settings(
    libraryDependencies ++= Seq(
      "org.hibernate.orm" % "hibernate-core" % "6.6.13.Final",
      "org.hibernate.orm" % "hibernate-envers" % "6.6.13.Final"
    )
  )

lazy val rest = (project in file("rest"))
  .dependsOn(domain)

lazy val application = (project in file("application"))
  .dependsOn(rest)
  .settings(
    ss = excludeDependencies ++= Seq(
      "org.hibernate.orm" % "hibernate-core",
      "org.hibernate.orm" % "hibernate-envers",
      "jakarta.validation" % "jakarta.validation-api",
      "jakarta.enterprise" % "jakarta.enterprise.cdi-api",
      "jakarta.ws.rs" % "jakarta.ws.rs-api",
      "commons-io" % "commons-io",
      "org.apache.commons" % "commons-lang3",
      "jakarta.platform" % "jakarta.jakartaee-api",
      "org.jboss.resteasy" % "resteasy-client",
      "net.bytebuddy" % "byte-buddy",
      "org.slf4j" % "slf4j-api",
      "org.jboss.resteasy" % "resteasy-jackson2-provider"
    ),
    Compile / packageBin := {
      val jarOutput = (Compile / packageBin).value
      val cp = (Compile / dependencyClasspath).value.map(_.data)

      val warOutputDir = baseDirectory.value / "target/deployment/webapp.war"
      IO.delete(warOutputDir)

      val webInf = warOutputDir / "WEB-INF"
      val libDir = webInf / "lib"
      val classesDir = webInf / "classes"

      IO.createDirectory(libDir)
      IO.createDirectory(classesDir)

      val jarsToCopy = cp.filter(_.getName.endsWith(".jar"))
      jarsToCopy.foreach(jar => IO.copyFile(jar, libDir / jar.getName))

      val subprojectClassDirs = Seq("library/scala-universe2", "library/json-mapper2", "system", "domain", "rest").map { subproject =>
        baseDirectory.value / ".." / subproject / "target" / "scala-3.6.4" / "classes"
      }

      subprojectClassDirs.foreach { classDir =>
        if (classDir.exists()) {
          IO.copyDirectory(classDir, classesDir)
        }
      }
      
      val resourceDirs = (Compile / resourceDirectories).value
      resourceDirs.foreach { resDir =>
        if (resDir.exists()) IO.copyDirectory(resDir, classesDir)
      }

      val webappSrc = baseDirectory.value / "src" / "main" / "webapp" / "WEB-INF"
      if (webappSrc.exists()) {
        IO.copyDirectory(webappSrc, webInf)
      }

      jarOutput
    },
    Compile / redeploy := {
      val base = baseDirectory.value
      val cliPath = "D:\\Development\\wildfly-preview-35.0.0.Final\\bin\\jboss-cli.bat" 

      val command = Seq(cliPath, "--connect", "--command=/deployment=webapp.war:redeploy")
      val exitCode = Process(command, base).!

      if (exitCode != 0) sys.error(s"JBoss CLI failed with exit code $exitCode")
    },
    Compile / redeploy := (Compile / redeploy).dependsOn(Compile / packageBin).value
)