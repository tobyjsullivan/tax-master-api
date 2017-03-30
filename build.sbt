// Dependencies
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"
val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.4.17"
val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % "2.4.17"
val akkaHttpCore = "com.typesafe.akka" %% "akka-http-core" % "10.0.5"
val akkaHttpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.5"
val jodaTime = "joda-time" % "joda-time" % "2.9.9"
val jodaMoney = "org.joda" % "joda-money" % "0.12"
val aws = "com.amazonaws" % "aws-java-sdk" % "1.11.109"

// Our main build settings for the tax-master-api project
lazy val root = (project in file(".")).
  settings(
    name := "tax-master-api",

    // Latest Scala version at time of authorship
    scalaVersion := "2.12.1",

    // Delineate dependencies
    libraryDependencies ++= Seq(
      scalaTest,
      akkaActor,
      akkaTestkit,
      akkaHttpCore,
      akkaHttpSprayJson,
      jodaTime,
      jodaMoney,
      aws
    )
  )
