package nl.os3.ls

import akka.actor.{ActorContext, ActorRef, ActorSystem, Props}
import akka.routing.RoundRobinPool
import com.typesafe.config.{Config, ConfigFactory}

object WorkerRouterCreator {
  val config: Config = ConfigFactory.load()
  val routingType = config.getConfig("experiment").getString("type")
  val noWorkerInstances = config.getConfig("experiment").getInt("no_worker")

  def createRouter(context: ActorSystem): ActorRef = {
    val routingAlgorithm = RouterSelector.selectRouter(routingType, noWorkerInstances)
    context.actorOf(routingAlgorithm.props(Props[WorkerActor]), "workerActor")
  }

  def createWorkerRouter(context: ActorContext): ActorRef = {
    val routingAlgorithm = RouterSelector.selectRouter(routingType, noWorkerInstances, true)
    context.actorOf(routingAlgorithm.props(Props[WorkerActor]), "workerActor")
  }
}
