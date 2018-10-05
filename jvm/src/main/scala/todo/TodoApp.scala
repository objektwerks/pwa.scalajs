package todo

import cats.effect._
import doobie.hikari.HikariTransactor
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import org.http4s.server.blaze._

import scala.concurrent.ExecutionContext.Implicits.global

object TodoApp extends StreamApp[IO] {
  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    for {
      conf <- Stream.eval(TodoConfig.load("todo.conf"))
      server = conf.server
      db = conf.database
      xa <- Stream.eval(HikariTransactor.newHikariTransactor[IO](db.driver, db.url, db.user, db.password))
      repository = TodoRepository(xa, db.schema)
      service = TodoService(repository).instance
      exitCode <- BlazeBuilder[IO]
        .bindHttp(server.port, server.host)
        .mountService(service, "/api/v1")
        .serve
    } yield {
      sys.addShutdownHook(requestShutdown.unsafeRunSync)
      exitCode
    }
  }
}