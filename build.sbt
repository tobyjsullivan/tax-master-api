// Dependencies
val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.4.17"
val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.0.5"
val akkaHttpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.5"
val jodaTime = "joda-time" % "joda-time" % "2.9.9"
val jodaMoney = "org.joda" % "joda-money" % "0.12"

// Our main build settings for the tax-master-api project
lazy val root = (project in file(".")).
  settings(
    name := "tax-master-api",

    // Latest Scala version at time of authorship
    scalaVersion := "2.12.1",

    // Delineate dependencies
    libraryDependencies ++= Seq(
      akkaActor,
      akkaHttp,
      akkaHttpSprayJson,
      jodaTime,
      jodaMoney
    )
  )
