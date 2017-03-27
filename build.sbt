// Akka
val akka_actor = "com.typesafe.akka" %% "akka-actor" % "2.4.17"

// Akka HTTP
val akka_http = "com.typesafe.akka" %% "akka-http" % "10.0.5"

// Our main build settings for the tax-master-api project
lazy val root = (project in file(".")).
  settings(
    name := "tax-master-api",

    // Latest Scala version at time of authorship
    scalaVersion := "2.12.1",

    // Delineate dependencies
    libraryDependencies ++= Seq(
      akka_actor,
      akka_http
    )
  )
