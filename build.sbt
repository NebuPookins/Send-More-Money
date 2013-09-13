name := "Send More Money"

version := "0.1"

scalaVersion := "2.9.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
 
libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0"

libraryDependencies += "com.typesafe.akka" % "akka-testkit" % "2.0" % "test"

libraryDependencies += "net.liftweb" %% "lift-json" % "2.5.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.2" % "test"

libraryDependencies += "commons-codec" % "commons-codec" % "1.3"

libraryDependencies += "oauth.signpost" % "signpost-core" % "1.2.1.2"
