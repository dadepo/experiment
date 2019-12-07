package nl.os3.ls

import java.math.BigInteger
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, Props}
import akka.event.Logging
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.{ExecutionContext, Future}

object WorkerActor {
  def apply() = Props[WorkerActor]
}

class WorkerActor extends Actor {

  val config: Config = ConfigFactory.load()

  val log = Logging(context.system, this)

  implicit val executionContext: ExecutionContext = context.system.dispatchers.lookup("custom-dispatcher")

  def performIO(uuid: String, count: Int, ioThreadSleep: Long) = {

    Future {
      log.info(s"{${this.self.path}} received io task count: $count with id:$uuid at ${LocalDateTime.now()}")
      TimeUnit.MILLISECONDS.sleep(ioThreadSleep)
      log.info(s"{${this.self.path}} done processing IO bound task count: $count with id:$uuid at ${LocalDateTime.now()}")
    }
  }

  def performCPU(uuid: String, count: Int, fibCompute:Int): Unit = {

    log.info(s"{${this.self.path}} received cpu task count: $count with id:$uuid at ${LocalDateTime.now()}")
    def fib(n: BigInteger): BigInteger = if (n.compareTo(BigInteger.ONE) == -1 || n.compareTo(BigInteger.ONE) == 0) n
    else fib(n.subtract(BigInteger.ONE)).add(fib(n.subtract(BigInteger.ONE).subtract(BigInteger.ONE)))

    fib(BigInteger.valueOf(fibCompute))
    log.info(s"{${this.self.path}} done processing CPU bound task count: $count with id:$uuid at ${LocalDateTime.now()}")
  }

  override def receive: Receive = {
    case IOTasks(uuid, taskId, ioThreadSleep) => performIO(uuid, taskId, ioThreadSleep)
    case CPUTasks(uuid, taskId, fibCompute) => performCPU(uuid, taskId, fibCompute)
  }
}
