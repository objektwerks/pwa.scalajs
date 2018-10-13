package todo.implicits

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import todo.{Count, Id, Todo}

object TodoCirceImplicits {
  implicit val todoDecoder: Decoder[Todo] = deriveDecoder[Todo]
  implicit val todoEncoder: Encoder[Todo] = deriveEncoder[Todo]

  implicit val todosDecoder: Decoder[List[Todo]] = deriveDecoder[List[Todo]]
  implicit val todosEncoder: Encoder[List[Todo]] = deriveEncoder[List[Todo]]

  implicit val idDecoder: Decoder[Id] = deriveDecoder[Id]
  implicit val idEncoder: Encoder[Id] = deriveEncoder[Id]

  implicit val countDecoder: Decoder[Count] = deriveDecoder[Count]
  implicit val countEncoder: Encoder[Count] = deriveEncoder[Count]
}