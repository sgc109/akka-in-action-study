//enablePlugins(JavaServerAppPackaging)

name := "chapter-02"

version := "0.1"

organization := "com.sungho"

scalaVersion := "2.13.3"

libraryDependencies ++= {
  val akkaVersion = "2.6.8"
  val akkaHttpVersion = "10.2.0"
  Seq(
    "com.typesafe.akka" % "akka" % "2.1.4",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json"  % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "org.scalatest" %% "scalatest" % "3.2.0" % "test"
  )
}
