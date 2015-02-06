//////////////////////////////////////////
////////// Build cfg

name := "scala-zmq-test"

organization := "se.gigurra"

scalaVersion := "2.11.4"

version := "SNAPSHOT"

crossPaths := false

libraryDependencies ++= Seq(
  "org.zeromq" % "jeromq" % "0.3.4"
)

EclipseKeys.withSource := true
