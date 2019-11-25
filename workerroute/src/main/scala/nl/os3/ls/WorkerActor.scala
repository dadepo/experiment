package nl.os3.ls

import akka.actor.{Actor, Props, Timers}

object WorkerActor {
  def apply() = Props[WorkerActor]
}
import scala.concurrent.duration._

class WorkerActor extends Actor with Timers {

  timers.startPeriodicTimer("TickKey", "hello", 5000.millis)

  override def receive: Receive = {
    case "hello" => {
      println("I am worker app")
      println(self.path.toString)
    }
    case "yo" => println("Yo hommy!")
  }
}
