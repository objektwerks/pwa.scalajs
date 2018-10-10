package todo

import cats.effect._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._
import todo.Todo._
import todo.implicits.TodoHttp4sCirceImplicits._

class TodoService(repository: TodoRepository) {
  val instance = HttpService[IO] {
    case GET -> Root / "todos" => Ok(repository.select.asJson.noSpaces)

    case request @ POST -> Root / "todos" =>
      for {
        todo <- request.as[Todo]
        response <- Ok(repository.insert(todo).asJson.noSpaces)
      } yield response

    case request @ PUT -> Root / "todos" =>
      for {
        todo <- request.as[Todo]
        response <- Ok(repository.update(todo).asJson.noSpaces)
      } yield response

    case DELETE -> Root / "todos" / IntVar(id) => Ok(repository.delete(id).asJson.noSpaces)
  }
}

object TodoService {
  def apply(repository: TodoRepository): TodoService = new TodoService(repository)
}