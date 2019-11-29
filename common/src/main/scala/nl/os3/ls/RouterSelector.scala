package nl.os3.ls

import scala.concurrent.duration._
import akka.routing.{ConsistentHashingPool, RandomPool, RoundRobinPool, ScatterGatherFirstCompletedPool, SmallestMailboxPool}

object RouterSelector {

  def selectRouter(routingType: String, noWorkerInstances: Int, isworker: Boolean = false) = {
    routingType match {
      case "round-robin" => RoundRobinPool(if (isworker) noWorkerInstances else 0)
      case "random" => RandomPool(if (isworker) noWorkerInstances else 0)
      case "smallest-mail-box" => SmallestMailboxPool(if (isworker) noWorkerInstances else 0)
      case "scatter-gather-first-completed" => ScatterGatherFirstCompletedPool(nrOfInstances = noWorkerInstances, within = 30 seconds)
      case "consistent-hashing" => ConsistentHashingPool(if (isworker) noWorkerInstances else 0)
    }
  }
}
