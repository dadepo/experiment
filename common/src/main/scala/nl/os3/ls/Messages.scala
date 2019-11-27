package nl.os3.ls

sealed trait Messages

case object Yo extends Messages

sealed trait Tasks

case class IOTasks(taskId:Int)
case class CPUTasks(taskId:Int)