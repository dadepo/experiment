
name := "experiment-app"
organization in ThisBuild := "nl.os3.ls"
scalaVersion in ThisBuild := "2.12.7"

val akkaVersion = "2.5.26"

lazy val dependencies = new {
  val akkaCluser = "com.typesafe.akka" %% "akka-cluster"                        % akkaVersion
  val akkaCluserTools = "com.typesafe.akka" %% "akka-cluster-tools"                  % akkaVersion
  val akkaSl4j = "com.typesafe.akka" %% "akka-slf4j"                          % akkaVersion
  val akkaLogback = "ch.qos.logback"    %  "logback-classic"                     % "1.2.3"
}

lazy val commonDependencies = Seq(
  dependencies.akkaCluserTools,
  dependencies.akkaCluser,
  dependencies.akkaSl4j,
  dependencies.akkaLogback
)


lazy val global = project
  .in(file("."))
  .settings(
    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint"),
    resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/")
  )
  .aggregate(
    common,
    seedroute,
    workerroute
  )

lazy val common = project.settings(
  name := "common",
  libraryDependencies ++= commonDependencies
)

lazy val seedroute = project.settings(
  name := "seedroute",
  libraryDependencies ++= commonDependencies
).enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .dependsOn(
  common
)

lazy val workerroute = project.settings(
  name := "workerroute",
  libraryDependencies ++= commonDependencies
).enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .dependsOn(
  common
)


//import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings
//import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm
//
//val akkaVersion = "2.6.0"
//
//lazy val `experiment-app` = project
//  .in(file("."))
//  .settings(multiJvmSettings: _*)
//  .settings(
//    organization := "com.typesafe.akka.samples",
//    scalaVersion := "2.13.1",
//    Compile / scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint"),
//    Compile / javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
//    run / javaOptions ++= Seq("-Xms128m", "-Xmx1024m", "-Djava.library.path=./target/native"),
//    libraryDependencies ++= Seq(
//      "com.typesafe.akka" %% "akka-actor"           % akkaVersion,
//      "com.typesafe.akka" %% "akka-cluster"         % akkaVersion,
//      "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion,
//      "com.typesafe.akka" %% "akka-multi-node-testkit"    % akkaVersion,
//      "ch.qos.logback"    %  "logback-classic"             % "1.2.3",
//      "org.scalatest"     %% "scalatest"                  % "3.0.8"     % Test,
//      "com.typesafe.akka" %% "akka-actor-testkit"   % akkaVersion % Test),
//    run / fork := false,
//    Global / cancelable := false,
//    // disable parallel tests
//    Test / parallelExecution := false,
//    licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))
//  )
//  .configs (MultiJvm)



//enablePlugins(JavaAppPackaging)
//enablePlugins(DockerPlugin)
//enablePlugins(AshScriptPlugin)
//
dockerBaseImage := "java:8-jre-alpine"
version in Docker := "latest"
dockerExposedPorts := Seq(8000)
dockerRepository := Some("dadepo")