organization := "com.typesafe.slick"

name := "slick-examples"

version := "2.0.1"

scalaVersion := "2.10.3"

scalacOptions += "-deprecation"

resolvers += "repo.codahale.com" at "http://repo.codahale.com"

// scala-compiler is declared as an optional dependency by Slick.
// You need to add it explicitly to your own project if you want
// to use the direct embedding (as in SimpleExample.scala here).
libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-compiler" % _)

libraryDependencies ++= List(
//  "com.typesafe.slick" %% "slick" % "2.1.0-M1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.3.170",
  "org.xerial" % "sqlite-jdbc" % "3.7.2",
  "org.mongodb" %% "casbah" % "2.7.1",
  "com.novus" %% "salat" % "1.9.8"
//  "com.codahale" %% "jerkson" % "0.5.0"
)

unmanagedBase :=  baseDirectory.value / "../slick/target/scala-2.10"
