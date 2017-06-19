package testtask.actors

import akka.actor.Actor
import testtask.models.RememberString

class ReminderActor extends Actor {
  def receive: Receive = active(Seq())

  def active(storage: Seq[String]): Receive = {
    case RememberString(str) => {
      context become active(storage :+ str)
    }
    case "Remind" =>
      sender() ! storage
  }
}
