package todo

import java.util.Date

case class Todo(id: Int = 0,
                task: String,
                opened: Long = new Date().getTime,
                closed: Long = new Date().getTime)

case class Id(value: Int)

case class Count(value: Int)