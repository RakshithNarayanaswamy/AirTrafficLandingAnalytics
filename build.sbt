ThisBuild / version := "0.1.0-SNAPSHOT"

name := "airtrafficlanding"      // or "TRAFFIC-LANDING-ANALYTICS"

scalaVersion := "2.13.17"

// Compile/run with JDK 17 (required for the Spring 2026 build)
ThisBuild / scalacOptions ++= Seq("-java-output-version", "17")
ThisBuild / javacOptions ++= Seq("-source", "17", "-target", "17")

// Optional unmanaged jars (if any) live here.
unmanagedBase := baseDirectory.value / "lib"

libraryDependencies ++= Seq(
  // TableParser for Spark (course library)
  "com.phasmidsoftware" %% "tableparser-spark" % "1.2.5",

  // Spark (course requirement)
  "org.apache.spark" %% "spark-core" % "4.0.1",
  "org.apache.spark" %% "spark-sql"  % "4.0.1",

  // Akka (for streaming & HTTP later)
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.5",
  "com.typesafe.akka" %% "akka-stream"      % "2.8.5",
  "com.typesafe.akka" %% "akka-http"        % "10.5.3",

  "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.8.5" % Test,

  // Logging (needed for LazyLogging)
  "ch.qos.logback" % "logback-classic" % "1.5.16",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",

  // Tests
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,

  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.3.0",
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",

)

// Main entry point
Compile / mainClass := Some("air.app.Run")

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-encoding", "utf8"
)