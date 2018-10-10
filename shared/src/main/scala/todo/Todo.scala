package todo

import java.sql.Timestamp
import java.time.Instant

object Todo {
  case class Todo(id: Int = 0,
                  task: String,
                  opened: Timestamp = Timestamp.from(Instant.now),
                  closed: Timestamp = Timestamp.from(Instant.now))

  case class Id(id: Int)

  case class Count(count: Int)
}