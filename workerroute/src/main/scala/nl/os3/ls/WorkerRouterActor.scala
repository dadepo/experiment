package nl.os3.ls

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef, Props, Timers}
import akka.routing.RoundRobinPool

object WorkerRouterActor {
  def apply() = Props[WorkerRouterActor]
}

class WorkerRouterActor extends Actor with Timers {

  val router: ActorRef = WorkerRouterCreator.createWorkerRouter(context)

  override def receive: Receive = {
    case iotask @ IOTasks(uuid, i, _) => {
      println(s"routing io task count: $i with id:$uuid to worker at ${LocalDateTime.now()}")
      router !  iotask
    }
    case cputask @ CPUTasks(uuid, i,_) => {
      println(s"routing cpu task count: $i with id:$uuid to worker at ${LocalDateTime.now()}")
      router !  cputask
    }
  }
}
