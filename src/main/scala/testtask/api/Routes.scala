package testtask.api

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

import testtask.models.AskGoogle

trait Routes extends JsonSerializer {
  implicit val executionContext: ExecutionContext
  implicit val timeout: Timeout = 5.seconds
  val googler: ActorRef
  val reminder: ActorRef

  def apiRoutes: Route =
    path("ask") {
      get {
        parameters('q.as[String]) { (q) =>
          complete {
            val res = (googler ? AskGoogle(q)).mapTo[Either[String, Seq[String]]]
            res.map[ToResponseMarshallable](resp => resp)
          }
        }
      }
    } ~
    path("remind") {
      get {
        complete {
          val res = (reminder ? "Remind").mapTo[Seq[String]]
          res.map[ToResponseMarshallable](resp => resp)
        }
      }
    }
}
