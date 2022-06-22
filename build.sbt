ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "hh-parse-vacancies"
  )


val akkaHttpVersion = "10.2.9"
val akkaStreamsVersion = "2.6.19"
val apachePoiVersion = "5.2.0"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaStreamsVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "org.apache.poi" % "poi" % apachePoiVersion,
  "org.apache.poi" % "poi-ooxml" % apachePoiVersion
)

