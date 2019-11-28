package nl.os3.ls

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

import collection.JavaConverters._
import scala.collection.mutable

object WorkerApp extends App {
//  val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=1601")
//    .withFallback(ConfigFactory.load())

  val config: Config = ConfigFactory.load()

  val hostIp = config.getString("akka.remote.netty.tcp.hostname")
  val hostPost = config.getString("akka.remote.netty.tcp.port")
  val clusterName = config.getString("clustering.name")
  val seed = config.getList("akka.cluster.seed-nodes").get(0).render()


  println("I am worker app")
  println("I am part of a cluster: {}", clusterName)
  println("I am running on ip: {}", hostIp)
  println("I am running on port: {}", hostPost)
  println("I am connecting to seed on : {}", seed)


  implicit val system: ActorSystem = ActorSystem(clusterName, config)

//  system.actorOf(WorkerRouterActor(), "workera")
//  system.actorOf(WorkerRouterActor(), "workerb")
}
