package todo

import java.sql.Timestamp
import java.time.Instant

object Todo {
  case class Todo(id: Int = 0,
                  task: String,
                  opened: Long = Timestamp.from(Instant.now).getTime,
                  closed: Long = Timestamp.from(Instant.now).getTime)

  case class Id(id: Int)

  case class Count(count: Int)
}