package nl.os3.ls

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.routing.FromConfig
import com.typesafe.config.{Config, ConfigFactory}


import scala.concurrent.duration._


object SeedApp extends App {
  val config: Config = ConfigFactory.load()
  val hostIp = config.getString("clustering.host-ip")
  val hostPost = config.getString("clustering.host-port")
  val clusterName = config.getString("clustering.name")
  val routerSeedIp = config.getString("clustering.router-seed-ip")
  val routerSeedPort = config.getString("clustering.router-seed-port")

  println("I am seed app")
  println("I am part of a cluster: {}", clusterName)
  println("I am running on host-ip: {}", hostIp)
  println("I am running on host-port: {}", hostPost)
  println("I am connecting to seed on router-seed-ip: {}", routerSeedIp)
  println("I am connecting to seed on router-seed-port: {}", routerSeedPort)

  implicit val system: ActorSystem = ActorSystem(clusterName, config)

  val workRouter: ActorRef = system.actorOf(FromConfig.props(Props.empty), "workRouter")

  import system.dispatcher
  system.scheduler.schedule(Duration.Zero, 2000.milliseconds, workRouter, "yo")

  system.actorOf(SeedActor())

}
