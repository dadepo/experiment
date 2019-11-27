package nl.os3.ls

sealed trait Messages

case object Yo extends Messages

sealed trait Tasks

case class IOTasks(taskId:Int) extends Tasks
case class CPUTasks(taskId:Int) extends Tasks

case class ExperimentData(cpuTasks:Int, ioTasks:Int, delay:Int)