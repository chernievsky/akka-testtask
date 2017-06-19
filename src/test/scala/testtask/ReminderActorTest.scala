package testtask

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import testtask.actors.ReminderActor
import testtask.models.RememberString

class ReminderActorTest extends TestKit(ActorSystem("Test")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A Reminder actor" must {

    "send empty response at the start" in {
      val reminder = system.actorOf(Props[ReminderActor], "test-reminder1")
      reminder ! "Remind"
      expectMsg(Seq[String]())
    }

    "send all queries that have been remembered" in {
      val reminder = system.actorOf(Props[ReminderActor], "test-reminder2")
      reminder ! RememberString("scala")
      reminder ! RememberString("akka")
      reminder ! RememberString("slick")
      reminder ! "Remind"
      expectMsg(Seq("scala", "akka", "slick"))
    }

  }
}