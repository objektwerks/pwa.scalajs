package todo

import java.sql.Timestamp
import java.time.Instant

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

object Todo {
  case class Todo(id: Int = 0,
                  task: String,
                  opened: Timestamp = Timestamp.from(Instant.now),
                  closed: Timestamp = Timestamp.from(Instant.now))

  case class Id(id: Int)

  case class Count(count: Int)

  implicit val timestampEncoderDecoder: Encoder[Timestamp] with Decoder[Timestamp] = new Encoder[Timestamp] with Decoder[Timestamp] {
    override def apply(timestamp: Timestamp): Json = Encoder.encodeLong.apply(timestamp.getTime)

    override def apply(cursor: HCursor): Result[Timestamp] = Decoder.decodeLong.map(long => new Timestamp(long)).apply(cursor)
  }
}