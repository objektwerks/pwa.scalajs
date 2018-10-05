package todo

import java.sql.Timestamp
import java.time.Instant

import cats.effect.IO
import io.circe.Decoder.Result
import io.circe.generic.auto._
import io.circe.{Decoder, Encoder, HCursor, Json}
import org.http4s.circe._

case class Todo(id: Int = 0, task: String, opened: Timestamp = Timestamp.from(Instant.now), closed: Timestamp = Timestamp.from(Instant.now))

object Todo {
  implicit val todoDecoder = jsonOf[IO, Todo]
  implicit val todoEncoder = jsonEncoderOf[IO, Todo]

  implicit val todoIdDecoder = jsonOf[IO, Int]
  implicit val todoIdEncoder = jsonEncoderOf[IO, Int]

  implicit val todoListDecoder = jsonOf[IO, List[Todo]]

  implicit val timestampEncoderDecoder: Encoder[Timestamp] with Decoder[Timestamp] = new Encoder[Timestamp] with Decoder[Timestamp] {
    override def apply(timestamp: Timestamp): Json = Encoder.encodeLong.apply(timestamp.getTime)
    override def apply(cursor: HCursor): Result[Timestamp] = Decoder.decodeLong.map(long => new Timestamp(long)).apply(cursor)
  }

  case class Id(id: Int)
  implicit val idDecoder = jsonOf[IO, Id]
  implicit val idEncoder = jsonEncoderOf[IO, Id]

  case class Count(count: Int)
  implicit val countDecoder = jsonOf[IO, Count]
  implicit val countEncoder = jsonEncoderOf[IO, Count]
}