package nl.os3.ls

import java.math.BigInteger

import akka.actor.{Actor, Props}
import akka.event.Logging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object WorkerActor {
  def apply() = Props[WorkerActor]
}

class WorkerActor extends Actor {

  val log = Logging(context.system, this)

  def performIO(id: Int) = Future {
    Thread.sleep(5000)
    println(s"Done processing IO bound task with id : $id")
  }

  def performCPU(id: Int) = {

    def fib(n: BigInteger): BigInteger = if (n.compareTo(BigInteger.ONE) == -1 || n.compareTo(BigInteger.ONE) == 0) n
    else fib(n.subtract(BigInteger.ONE)).add(fib(n.subtract(BigInteger.ONE).subtract(BigInteger.ONE)))

    fib(BigInteger.valueOf(10))
    println(s"Done processing CPU bound task with id : $id")
  }

  override def receive: Receive = {
    case IOTasks(taskId) => performIO(taskId)
    case CPUTasks(taskId) => performCPU(taskId)
  }
}
