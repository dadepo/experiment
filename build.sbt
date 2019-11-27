
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
    resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"),
    libraryDependencies ++= commonDependencies ++ Seq(
      "com.typesafe.akka" %% "akka-slf4j"                          % akkaVersion,
      "ch.qos.logback"    %  "logback-classic"                     % "1.2.3",
      "net.logstash.logback" % "logstash-logback-encoder" % "4.11",
      "io.dropwizard.metrics" % "metrics-core" % "3.2.2",
      Cinnamon.library.cinnamonCHMetrics3,
      Cinnamon.library.cinnamonAkka,
      Cinnamon.library.cinnamonCHMetricsElasticsearchReporter,
      Cinnamon.library.cinnamonSlf4jEvents,
    )
  ).enablePlugins(Cinnamon)
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
  libraryDependencies ++= commonDependencies ++ Seq(
    "com.typesafe.akka" %% "akka-http"   % "10.1.10",
    "com.typesafe.akka" %% "akka-stream" % "2.5.23",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.10"
  )
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

dockerBaseImage := "java:8-jre-alpine"
version in Docker := "latest"
dockerExposedPorts := Seq(8000)
dockerRepository := Some("dadepo")

cinnamon in run := true
cinnamon in test := true

// Set the Cinnamon Agent log level
cinnamonLogLevel := "INFO"