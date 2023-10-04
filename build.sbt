import Dependencies._

ThisBuild / scalaVersion     := "2.13.11"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "sune-tzu-meditations",
    libraryDependencies ++= List(
      munit % Test,
      "com.google.cloud" % "google-cloud-texttospeech" % "2.24.0",
      "com.google.cloud" % "google-cloud-secretmanager" % "2.23.0",
      "org.scala-lang.modules" %% "scala-xml" % "2.2.0",
      "commons-io" % "commons-io" % "2.13.0",
      "clj-fuzzy" % "clj-fuzzy" % "0.4.1"
    ),
    resolvers += ("clojars.org" at "http://clojars.org/repo/").withAllowInsecureProtocol(true)
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
