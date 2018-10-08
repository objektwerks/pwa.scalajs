package todo

import cats.data.Kleisli
import cats.effect.IO
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._
import org.http4s.{Header, HttpService, Request, StaticFile, Status}

object WebServices {
  private val noCacheHeader = Header("Cache-Control", "no-cache, no-store, must-revalidate")

  private def addHeader(service: HttpService[IO], header: Header): HttpService[IO] = Kleisli { request: Request[IO] =>
    service(request).map {
      case Status.Successful(response) => response.putHeaders(header)
      case response => response
    }
  }

  private val indexService = HttpService[IO] {
    case request @ GET -> Root => StaticFile.fromResource("/index.html", Some(request)).getOrElseF(NotFound())
  }
  val indexServiceWithNoCacheHeader = addHeader(indexService, noCacheHeader)

  private val resourceService = HttpService[IO] {
    case request @ GET -> Root / path if List(".ico", ".png", ".css", ".js", ".webmanifest")
      .exists(path.endsWith) => StaticFile.fromResource("/" + path, Some(request))
      .getOrElseF(NotFound())
  }
  val resourceServiceWithNoCacheHeader = addHeader(resourceService, noCacheHeader)
}