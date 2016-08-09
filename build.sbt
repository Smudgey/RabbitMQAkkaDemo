name := "rabbitmqAkkaConsoleDemo"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "The New Motion Public Repo" at "http://nexus.thenewmotion.com/content/groups/public/"
libraryDependencies += "com.thenewmotion.akka" %% "akka-rabbitmq" % "2.3"
libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.4.9-RC2"