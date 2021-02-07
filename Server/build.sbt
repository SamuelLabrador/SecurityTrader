name := """Server"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SwaggerPlugin)

scalaVersion := "2.13.3"

val AkkaVersion = "2.6.11"

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += evolutions
libraryDependencies += jdbc
libraryDependencies += "com.h2database" % "h2" % "1.4.192"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.webjars" % "swagger-ui" % "3.35.0"
libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "8.0.23",
  "com.typesafe.akka" %% "akka-actor"  % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-slf4j"  % AkkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson" % AkkaVersion
)
swaggerDomainNameSpaces := Seq("models")

PlayKeys.devSettings += "play.server.http.port" -> "9000"
PlayKeys.devSettings += "play.server.https.port" -> "9443"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
