// Spray library requirements
val sprayRepo = "spray repo" at "http://repo.spray.io"
val spray = "io.spray" %% "spray-can" % "1.3.4"

// Our main build settings for the tax-master-api project
lazy val root = (project in file(".")).
  settings(
    name := "tax-master-api",

    // Latest Scala version at time of authorship
    scalaVersion := "2.11.8",

    // Delineate resolvers and dependencies
    resolvers += sprayRepo,
    libraryDependencies += spray
  )
