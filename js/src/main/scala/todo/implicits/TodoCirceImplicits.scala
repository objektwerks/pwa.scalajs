package todo.implicits

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import todo.Todo._

object TodoCirceImplicits {
  implicit val todoDecoder: Decoder[Todo] = deriveDecoder[Todo]
  implicit val todoEncoder: Encoder[Todo] = deriveEncoder[Todo]

  implicit val idDecoder: Decoder[Id] = deriveDecoder[Id]
  implicit val idEncoder: Encoder[Id] = deriveEncoder[Id]

  implicit val countDecoder: Decoder[Count] = deriveDecoder[Count]
  implicit val countEncoder: Encoder[Count] = deriveEncoder[Count]
}