package nl.os3.ls

import scala.concurrent.duration._
import akka.actor.{Actor, ActorRef, Props, Timers}
import akka.routing.FromConfig

object SeedActor {
  def apply() = Props[SeedActor]
}

class SeedActor extends Actor with Timers {

  override def receive: Receive = {
    case "hello" => {
      println("I am Seed App")
    }
  }
}
