package todo.implicits

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s.circe._
import todo.{Count, Id, Todo}

object TodoHttp4sCirceImplicits {
  implicit val todoDecoder = jsonOf[IO, Todo]
  implicit val todoEncoder = jsonEncoderOf[IO, Todo]

  implicit val todoIdDecoder = jsonOf[IO, Int]
  implicit val todoIdEncoder = jsonEncoderOf[IO, Int]

  implicit val todoListDecoder = jsonOf[IO, List[Todo]]

  implicit val idDecoder = jsonOf[IO, Id]
  implicit val idEncoder = jsonEncoderOf[IO, Id]

  implicit val countDecoder = jsonOf[IO, Count]
  implicit val countEncoder = jsonEncoderOf[IO, Count]
}