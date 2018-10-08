package todo

import cats.data.Kleisli
import cats.effect.IO
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._
import org.http4s.{Header, HttpService, Request, StaticFile, Status}

class WebService {
  import WebService._

  private val service = HttpService[IO] {
    case request @ GET -> Root / path if List(".html", ".ico", ".png", ".css", ".js", ".webmanifest")
      .exists(path.endsWith) => StaticFile.fromResource("/" + path, Some(request))
      .getOrElseF(NotFound())
  }

  val instance = addHeader(service, noCacheHeader)
}

object WebService {
  private val noCacheHeader = Header("Cache-Control", "no-cache, no-store, must-revalidate")

  def apply(): WebService = new WebService()

  def addHeader(service: HttpService[IO], header: Header): HttpService[IO] = Kleisli { request: Request[IO] =>
    service(request).map {
      case Status.Successful(response) => response.putHeaders(header)
      case response => response
    }
  }
}