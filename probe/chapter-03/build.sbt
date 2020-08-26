name := "chapter-03"

version := "0.1"

scalaVersion := "2.13.3"

organization := "com.probe"

libraryDependencies ++= {
  val akkaVersion = "2.6.8"
  val scalaTestVersion = "3.2.2"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "org.scalatest" %% "scalatest-mustmatchers" % scalaTestVersion % "test"
  )
}