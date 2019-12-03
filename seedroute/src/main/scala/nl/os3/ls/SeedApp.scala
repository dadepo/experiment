package nl.os3.ls

import java.time.LocalDateTime

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.routing.{ConsistentHashingPool, FromConfig, RoundRobinPool}
import com.typesafe.config.{Config, ConfigFactory}
import akka.http.scaladsl.server.RouteResult._
import akka.pattern.ask

import scala.io.StdIn
import akka.actor.ActorSystem
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import nl.os3.ls.ClusterManager.GetMembers
import akka.util.Timeout

import scala.concurrent.duration._
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
  implicit lazy val timeout = Timeout(5.seconds)

  val clusterManager: ActorRef = system.actorOf(ClusterManager(), "clusterManager")

  val workRouter:ActorRef = SeedToWorkerNodeRouterCreator.createWorkRouter(system)

  system.actorOf(SeedActor())

  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val itemFormat = jsonFormat5(ExperimentData)

  val route: Route =
    concat(
      get {
        path("members") {
          val membersFuture: Future[List[String]] = (clusterManager ? GetMembers).mapTo[List[String]]
          val value = config.getConfig("experiment")
          onSuccess(membersFuture) { members =>
            complete(StatusCodes.OK, Map (
              "type" -> value.getString("type"),
              "no_worker" -> value.getString("no_worker"),
              "members" -> members.mkString("---===---")
            ))
          }
        }
      },
      post {
        path("start") {
          entity(as[ExperimentData]) { experimentData =>

            val uuid = java.util.UUID.randomUUID().toString

            val ioTasks = experimentData.ioTasks
            val ioThreadSleep = experimentData.ioThreadSleep

            val cpuTasks = experimentData.cpuTasks
            val fibCompute = experimentData.fibCompute

            val interval = experimentData.delay

            val iotaskFuture = Future {
              for (i <- 1 to ioTasks) {
                println(s"dispatching io task count: $i with id:$uuid at ${LocalDateTime.now()}")
                workRouter ! IOTasks(uuid, i, ioThreadSleep)
                if (interval != -1 || interval != 0) {
                  Thread.sleep(interval * 1000)
                }
              }
            }

            val cputaskFuture = Future {
              for (i <- 1 to cpuTasks) {
                println(s"dispatching cpu task count: $i with id:$uuid at ${LocalDateTime.now()}")
                workRouter ! CPUTasks(uuid, i, fibCompute)
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
