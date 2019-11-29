package nl.os3.ls

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.{ConsistentHashingPool, RandomPool, RoundRobinPool, ScatterGatherFirstCompletedPool, SmallestMailboxPool}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration._

object SeedToWorkerNodeRouterCreator {
  val config: Config = ConfigFactory.load()
  val routingType = config.getConfig("experiment").getString("type")
  val noWorkerInstances = config.getConfig("experiment").getInt("no_worker")


  def createWorkRouter(system: ActorSystem): ActorRef = {

    val routingAlgorithm = RouterSelector.selectRouter(routingType, noWorkerInstances)

    system.actorOf(
      ClusterRouterPool(
        routingAlgorithm,
        ClusterRouterPoolSettings(totalInstances = 2, maxInstancesPerNode = 1, allowLocalRoutees = false)
      ).props(Props[WorkerRouterActor]), name = "workRouter")
  }
}
