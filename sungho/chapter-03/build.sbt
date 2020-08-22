name := "chapter-03"

version := "0.1"

scalaVersion := "2.13.3"

val AkkaVersion = "2.6.8"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % AkkaVersion
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.0"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test
