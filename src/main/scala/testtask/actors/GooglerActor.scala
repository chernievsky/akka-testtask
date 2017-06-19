package testtask.actors

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.http.scaladsl.model.{HttpHeader, HttpRequest, Uri, headers}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import scala.concurrent.{ExecutionContext, Future}
import testtask.models.{AskGoogle, RememberString}

class GooglerActor(val reminder: ActorRef)(implicit val system: ActorSystem,
                                           implicit val materializer: ActorMaterializer,
                                           implicit val ec: ExecutionContext) extends Actor {
  val config = ConfigFactory.load()

  def getGoogleResponse(q: String): Future[Either[String, Seq[String]]] = {
    val request: HttpRequest = HttpRequest(
      uri = Uri(config.getString("google.entryPoint")).withQuery(Uri.Query("q" -> q)),
      headers = List(headers.`User-Agent`(config.getString("google.userAgent")))
    )

    Http().singleRequest(request).flatMap { response =>
      response.status match {
        case OK =>
          val html = Unmarshal(response.entity).to[String]
          val links = html.map(s => Right(parseLinks(s)))
          links
        case _ => Future.successful(Left(s"Can't get response from Google"))
      }
    }.recoverWith {
      case _ => Future.successful(Left(s"Can't connect to Google"))
    }
  }

  def parseLinks(html: String): Seq[String] = {
    val pattern = """<h3 class="r"><a href="(.*?)" onmousedown""".r
    val matches = pattern.findAllMatchIn(html)
    matches.map(r => r.group(1)).toSeq
  }

  def receive: Receive = {
    case AskGoogle(q) =>
      reminder ! RememberString(q)
      val origin = sender()
      val links = getGoogleResponse(q)
      links.foreach(resp => origin ! resp)
  }
}
