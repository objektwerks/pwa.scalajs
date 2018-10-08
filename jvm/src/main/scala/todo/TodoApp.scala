package todo

import cats.effect._
import doobie.hikari.HikariTransactor
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import org.http4s.server.blaze._
import org.http4s.server.middleware.{CORS, CORSConfig, GZip}

import scala.concurrent.ExecutionContext.Implicits.global

object TodoApp extends StreamApp[IO] {
  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    for {
      conf <- Stream.eval(TodoConfig.load("todo.conf"))
      server = conf.server
      db = conf.database
      cors = conf.cors
      corsx = CORSConfig(
        anyOrigin = cors.anyOrigin,
        allowCredentials = cors.allowCredentials,
        maxAge = cors.maxAge,
        allowedMethods = Some(cors.allowedMethods),
        allowedOrigins = cors.allowedOrigins
      )
      xa <- Stream.eval(HikariTransactor.newHikariTransactor[IO](db.driver, db.url, db.user, db.password))
      repository = TodoRepository(xa, db.schema)
      todoService = TodoService(repository).instance
      webService = WebService().instance
      exitCode <- BlazeBuilder[IO]
        .bindHttp(server.port, server.host)
        .mountService(GZip(CORS(todoService, corsx)), "/api/v1")
        .mountService(GZip(CORS(webService)))
        .serve
    } yield {
      sys.addShutdownHook(requestShutdown.unsafeRunSync)
      exitCode
    }
  }
}