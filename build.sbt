name := "akka-testtask"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.18",
  "com.typesafe.akka" %% "akka-http" % "10.0.7",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.7",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.18" % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)
