package nl.os3.ls

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

object WorkerApp extends App {
  val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=2553")
    .withFallback(ConfigFactory.load())


  val hostIp = config.getString("clustering.host-ip")
  val hostPost = config.getString("clustering.host-port")
  val clusterName = config.getString("clustering.name")
  val routerSeedIp = config.getString("clustering.router-seed-ip")
  val routerSeedPort = config.getString("clustering.router-seed-port")

  println("I am worker app")
  println("I am part of a cluster: {}", clusterName)
  println("I am running on host-ip: {}", hostIp)
  println("I am running on host-port: {}", hostPost)
  println("I am connecting to seed on router-seed-ip: {}", routerSeedIp)
  println("I am connecting to seed on router-seed-port: {}", routerSeedPort)


  implicit val system: ActorSystem = ActorSystem(clusterName, config)
  system.actorOf(WorkerActor(), "worker")
}
