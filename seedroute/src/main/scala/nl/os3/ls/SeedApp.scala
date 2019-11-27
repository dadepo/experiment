package nl.os3.ls

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.routing.FromConfig
import com.typesafe.config.{Config, ConfigFactory}


import scala.concurrent.duration._


object SeedApp extends App {
  val config: Config = ConfigFactory.load()

  val hostIp = config.getString("akka.remote.netty.tcp.hostname")
  val hostPost = config.getString("akka.remote.netty.tcp.port")
  val clusterName = config.getString("clustering.name")
  val seed = config.getList("akka.cluster.seed-nodes").get(0).render()

  println("I am seed app")
  println("I am part of a cluster: {}", clusterName)
  println("I am running on ip: {}", hostIp)
  println("I am running on port: {}", hostPost)
  println("I am connecting to seed on : {}", seed)

  implicit val system: ActorSystem = ActorSystem(clusterName, config)

  val workRouter: ActorRef = system.actorOf(FromConfig.props(Props.empty), "workRouter")

  import system.dispatcher

  system.scheduler.schedule(10 seconds, 30 seconds) {
    println("Starting..")
    for (i <- 1 to 6) {
      if (i % 2 == 0)
        workRouter ! CPUTasks(i)
      else
        workRouter ! IOTasks(i)
    }
    println("Done...")
  }

  system.actorOf(SeedActor())

}
