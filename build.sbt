import scala.sys.process.*

lazy val redeploy = taskKey[Unit]("Redeploys the WAR file into Wildfly")

Global / semanticdbEnabled := true

ThisBuild / scalaVersion := "3.7.0"
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / javacOptions ++= Seq("--release", "17")
ThisBuild / scalacOptions ++= Seq("-release", "17")

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
      "jakarta.validation" % "jakarta.validation-api" % "3.1.1",
      "jakarta.enterprise" % "jakarta.enterprise.cdi-api" % "4.1.0",
      "jakarta.persistence" % "jakarta.persistence-api" % "3.2.0",
      "jakarta.ws.rs" % "jakarta.ws.rs-api" % "4.0.0",
      "com.fasterxml.jackson.core" % "jackson-annotations" % "2.19.0",
      "org.apache.commons" % "commons-lang3" % "3.17.0",
      "org.apache.commons" % "commons-text" % "1.13.1",
      "org.hibernate.orm" % "hibernate-core" % "7.0.0.Beta5"
    )
  )

lazy val system = (project in file("system"))
  .dependsOn(jsonMapper2)
  .settings(
    libraryDependencies ++= Seq(
      "com.google.guava" % "guava" % "33.0.0-jre",
      "com.pgvector" % "pgvector" % "0.1.6",
      "com.yubico" % "webauthn-server-core" % "2.7.0",
      "commons-io" % "commons-io" % "2.16.0",
      "jakarta.platform" % "jakarta.jakartaee-api" % "10.0.0",
      "net.bytebuddy" % "byte-buddy" % "1.15.11",
      "org.apache.commons" % "commons-lang3" % "3.17.0",
      "org.apache.commons" % "commons-text" % "1.13.1",
      "org.hibernate.orm" % "hibernate-core" % "7.0.0.Beta5",
      "org.hibernate.orm" % "hibernate-envers" % "7.0.0.Beta5",
      "org.hibernate.orm" % "hibernate-vector" % "7.0.0.Beta5",
      "org.jboss.resteasy" % "resteasy-client" % "7.0.0.Beta1",
      "org.jboss.resteasy" % "resteasy-jackson2-provider" % "7.0.0.Beta1",
      "org.jsoup" % "jsoup" % "1.19.1",
      "org.postgresql" % "postgresql" % "42.7.5",
      "org.slf4j" % "slf4j-api" % "2.0.16",
      "org.thymeleaf" % "thymeleaf" % "2.0.21",
      "org.thymeleaf.extras" % "thymeleaf-extras-java8time" % "3.0.4.RELEASE"
    )
  )


lazy val domain = (project in file("domain"))
  .dependsOn(system)

lazy val rest = (project in file("rest"))
  .dependsOn(domain)

lazy val application = (project in file("application"))
  .dependsOn(rest)
  .settings(
    ss = excludeDependencies ++= Seq(
      "commons-io" % "commons-io",
      "jakarta.enterprise" % "jakarta.enterprise.cdi-api",
      "jakarta.platform" % "jakarta.jakartaee-api",
      "jakarta.validation" % "jakarta.validation-api",
      "jakarta.persistence" % "jakarta.persistence-api",
      "jakarta.ws.rs" % "jakarta.ws.rs-api",
      "net.bytebuddy" % "byte-buddy",
      "org.apache.commons" % "commons-lang3",
      "org.hibernate.orm" % "hibernate-core",
      "org.hibernate.orm" % "hibernate-envers",
      "org.jboss.resteasy" % "resteasy-client",
      "org.jboss.resteasy" % "resteasy-jackson2-provider",
      "org.postgresql" % "postgresql",
      "org.slf4j" % "slf4j-api"
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

      val subprojects = Seq("library/scala-universe2", "library/json-mapper2", "system", "domain", "rest")

      val subprojectJarFiles = subprojects.flatMap { subproject =>
        val jarDir = baseDirectory.value / ".." / subproject / "target" / "scala-3.7.0"
        if (jarDir.exists()) {
          (jarDir ** "*.jar").get
        } else {
          Seq.empty
        }
      }

      subprojectJarFiles.foreach { jar =>
        val targetFile = libDir / jar.name
        IO.copyFile(jar, targetFile)
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
      val cliPath = "D:\\Development\\wildfly-preview-36.0.0.Final\\bin\\jboss-cli.bat"

      val command = Seq(cliPath, "--connect", "--command=/deployment=webapp.war:redeploy")
      val exitCode = Process(command, base).!

      if (exitCode != 0) sys.error(s"JBoss CLI failed with exit code $exitCode")
    },
    Compile / redeploy := (Compile / redeploy).dependsOn(Compile / packageBin).value
)