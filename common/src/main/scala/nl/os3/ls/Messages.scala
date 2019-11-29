package nl.os3.ls

sealed trait Messages

case object Yo extends Messages

sealed trait Tasks

case class IOTasks(taskId:Int, ioThreadSleep: Int) extends Tasks
case class CPUTasks(taskId:Int, fibCompute:Int) extends Tasks

case class ExperimentData(cpuTasks:Int, ioTasks:Int, delay:Int, fibCompute:Int, ioThreadSleep: Int)

case class CreateWorkerRouter(amount:Int)