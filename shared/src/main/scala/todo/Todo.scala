package todo

import java.time.Instant

case class Todo(id: Int = 0,
                task: String,
                opened: Long = Instant.now.toEpochMilli,
                closed: Long = Instant.now.toEpochMilli)

case class Id(value: Int)

case class Count(value: Int)