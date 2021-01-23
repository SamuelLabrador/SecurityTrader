name := """Server"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SwaggerPlugin)

scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies ++= Seq(evolutions, jdbc)
libraryDependencies += "com.h2database" % "h2" % "1.4.192"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.webjars" % "swagger-ui" % "3.35.0"

swaggerDomainNameSpaces := Seq("models")
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
