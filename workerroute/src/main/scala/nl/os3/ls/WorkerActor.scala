package nl.os3.ls

import java.math.BigInteger
import java.time.LocalDateTime

import akka.actor.{Actor, Props}
import akka.event.Logging
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object WorkerActor {
  def apply() = Props[WorkerActor]
}

class WorkerActor extends Actor {

  val config: Config = ConfigFactory.load()

  val log = Logging(context.system, this)

  def performIO(id: Int, ioThreadSleep: Int) = Future {
    println(s"received io task with id:$id at ${LocalDateTime.now()}")
    Thread.sleep(ioThreadSleep * 1000)
    println(s"done processing IO bound task with id : $id at ${LocalDateTime.now()}")
  }

  def performCPU(id: Int, fibCompute:Int): Unit = {

    println(s"received cpu task with id:$id at ${LocalDateTime.now()}")
    def fib(n: BigInteger): BigInteger = if (n.compareTo(BigInteger.ONE) == -1 || n.compareTo(BigInteger.ONE) == 0) n
    else fib(n.subtract(BigInteger.ONE)).add(fib(n.subtract(BigInteger.ONE).subtract(BigInteger.ONE)))

    fib(BigInteger.valueOf(fibCompute))
    println(s"done processing CPU bound task with id : $id at ${LocalDateTime.now()}")
  }

  override def receive: Receive = {
    case IOTasks(taskId, ioThreadSleep) => performIO(taskId, ioThreadSleep)
    case CPUTasks(taskId, fibCompute) => performCPU(taskId, fibCompute)
  }
}
