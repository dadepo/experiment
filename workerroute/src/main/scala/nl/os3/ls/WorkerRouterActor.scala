package nl.os3.ls

import akka.actor.{Actor, ActorRef, Props, Timers}
import akka.routing.RoundRobinPool

object WorkerRouterActor {
  def apply() = Props[WorkerRouterActor]
}
import scala.concurrent.duration._

class WorkerRouterActor extends Actor with Timers {

  val router: ActorRef = context.actorOf(RoundRobinPool(5).props(Props[WorkerActor]), "workerActor")

  override def receive: Receive = {
    case iotask @ IOTasks(taskId) => {
      router ! iotask
    }
    case cputask @ CPUTasks(taskId) => {
      router ! cputask
    }
  }
}
