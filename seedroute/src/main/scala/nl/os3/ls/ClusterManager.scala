package nl.os3.ls

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.{Cluster, MemberStatus}
import nl.os3.ls.ClusterManager.GetMembers

object ClusterManager {

  case object GetMembers

  def apply() = Props[ClusterManager]
}

class ClusterManager extends Actor with ActorLogging {
  val cluster: Cluster = Cluster(context.system)

  override def receive: Receive = {
    case GetMembers => {
      sender() ! cluster.state.members.filter(_.status == MemberStatus.up)
        .map(member => s"${member.address.toString}:${member.uniqueAddress.toString}")
        .toList
    }
  }
}
