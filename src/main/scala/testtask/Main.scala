package testtask

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import scala.io.StdIn
import testtask.actors.{GooglerActor, ReminderActor}
import testtask.api.Routes

object Main extends App with Routes {
  val config = ConfigFactory.load()
  implicit val system = ActorSystem(name = "akka-testtask", config)
  implicit val streamMaterializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val log = system.log

  override val reminder = system.actorOf(Props[ReminderActor], "reminder")
  override val googler = system.actorOf(Props(new GooglerActor(reminder)), "googler")

  val host = config.getString("app.host")
  val port = config.getInt("app.port")

  val bindingFuture = Http().bindAndHandle(apiRoutes, host, port)
  bindingFuture.map(_.localAddress).map(addr => s"Bound to $addr").foreach(log.info)
  log.info("Press RETURN to stop...")

  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
