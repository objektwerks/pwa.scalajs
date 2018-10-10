package todo

import cats.effect._
import doobie.hikari.HikariTransactor
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import org.http4s.server.blaze._
import org.http4s.server.middleware.GZip

import scala.concurrent.ExecutionContext.Implicits.global

object TodoApp extends StreamApp[IO] {
  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    for {
      conf <- Stream.eval(TodoConfig.load("todo.conf"))
      server = conf.server
      db = conf.database
      xa <- Stream.eval(HikariTransactor.newHikariTransactor[IO](db.driver, db.url, db.user, db.password))
      repository = TodoRepository(xa, db.schema)
      todoService = TodoService(repository).instance
      exitCode <- BlazeBuilder[IO]
        .bindHttp(server.port, server.host)
        .mountService(GZip(todoService), "/api/v1")
        .mountService(GZip(WebServices.indexServiceWithNoCacheHeader), "/")
        .mountService(GZip(WebServices.resourceServiceWithNoCacheHeader), "/")
        .serve
    } yield {
      sys.addShutdownHook(requestShutdown.unsafeRunSync)
      exitCode
    }
  }
}