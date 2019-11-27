package nl.os3.ls

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.routing.FromConfig
import com.typesafe.config.{Config, ConfigFactory}
import akka.http.scaladsl.server.RouteResult._

import scala.io.StdIn
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
// for JSON serialization/deserialization following dependency is required:
// "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7"
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._


object SeedApp extends App {
  val config: Config = ConfigFactory.load()

  val hostIp = config.getString("akka.remote.netty.tcp.hostname")
  val hostPost = config.getString("akka.remote.netty.tcp.port")
  val clusterName = config.getString("clustering.name")
  val seed = config.getList("akka.cluster.seed-nodes").get(0).render()

  val address = config.getString("http.ip")
  val port = config.getInt("http.port")

  println("I am seed app")
  println("I am part of a cluster: {}", clusterName)
  println("I am running on ip: {}", hostIp)
  println("I am running on port: {}", hostPost)
  println("I am connecting to seed on : {}", seed)

  implicit val system: ActorSystem = ActorSystem(clusterName, config)

  val workRouter: ActorRef = system.actorOf(FromConfig.props(Props.empty), "workRouter")

  system.actorOf(SeedActor())

  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val itemFormat = jsonFormat3(ExperimentData)

  val route: Route =
    concat(
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Experiment App</h1>"))
      },
      post {
        path("start") {
          entity(as[ExperimentData]) { experimentData =>

            val ioTasks = experimentData.ioTasks
            val cpuTasks = experimentData.cpuTasks
            val interval = experimentData.delay

            val iotaskFuture = Future {
              for (i <- 1 to ioTasks) {
                workRouter ! IOTasks(i)
                if (interval != -1 || interval != 0) {
                  Thread.sleep(interval * 1000)
                }
              }
            }

            val cputaskFuture = Future {
              for (i <- 1 to cpuTasks) {
                workRouter ! CPUTasks(i)
                if (interval != -1 || interval != 0) {
                  Thread.sleep(interval * 1000)
                }
              }
            }

            val results = for {
              ioResult <- iotaskFuture
              cpuResult <- cputaskFuture
            } yield ()

            onComplete(results) { result =>
              complete(experimentData)
            }
          }
        }
      }
    )

  val bindingFuture = Http().bindAndHandle(route, address, port)

  println(s"Server online at http://localhost:8000/\nPress RETURN to stop...")

  Await.result(system.whenTerminated, Duration.Inf)

}
