package todo

import cats.effect._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._

class TodoService(repository: TodoRepository) {
  import Todo._

  val instance = HttpService[IO] {
    case GET -> Root / "todos" => Ok(repository.select.asJson)

    case request @ POST -> Root / "todos" =>
      for {
        todo <- request.as[Todo]
        response <- Ok(repository.insert(todo).asJson)
      } yield response

    case request @ PUT -> Root / "todos" =>
      for {
        todo <- request.as[Todo]
        response <- Ok(repository.update(todo).asJson)
      } yield response

    case DELETE -> Root / "todos" / IntVar(id) => Ok(repository.delete(id).asJson)
  }
}

object TodoService {
  def apply(repository: TodoRepository): TodoService = new TodoService(repository)
}