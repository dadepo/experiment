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

  def performIO(uuid: String, count: Int, ioThreadSleep: Int) = Future {
    println(s"received io task count:$count with id:$uuid at ${LocalDateTime.now()}")
    Thread.sleep(ioThreadSleep * 1000)
    println(s"done processing IO bound task count:$count with id:$uuid at ${LocalDateTime.now()}")
  }

  def performCPU(uuid: String, count: Int, fibCompute:Int): Unit = {

    println(s"received cpu task count:$count with id:$uuid at ${LocalDateTime.now()}")
    def fib(n: BigInteger): BigInteger = if (n.compareTo(BigInteger.ONE) == -1 || n.compareTo(BigInteger.ONE) == 0) n
    else fib(n.subtract(BigInteger.ONE)).add(fib(n.subtract(BigInteger.ONE).subtract(BigInteger.ONE)))

    fib(BigInteger.valueOf(fibCompute))
    println(s"done processing CPU bound task count:$count with id:$uuid at ${LocalDateTime.now()}")
  }

  override def receive: Receive = {
    case IOTasks(uuid, taskId, ioThreadSleep) => performIO(uuid, taskId, ioThreadSleep)
    case CPUTasks(uuid, taskId, fibCompute) => performCPU(uuid, taskId, fibCompute)
  }
}
