package todo

import cats.effect._
import doobie._
import doobie.implicits._

import scala.io.Source
import scala.util.Try

class TodoRepository(xa: Transactor[IO], schema: String) {
  import TodoRepository._

  Try(select.size) recover { case _ => init(schema) }

  def init(schemaPath: String): Int = {
    val schema = Source.fromInputStream(getClass.getResourceAsStream(schemaPath)).mkString
    Fragment.const(schema).update.run.transact(xa).unsafeRunSync
  }

  def select: List[Todo] = selectTodos.to[List].transact(xa).unsafeRunSync

  def insert(todo: Todo): Id = Id(insertTodo.toUpdate0((todo.task, todo.opened, todo.closed))
                               .withUniqueGeneratedKeys[Int]("id").transact(xa).unsafeRunSync)

  def update(todo: Todo): Count = Count(updateTodo.toUpdate0((todo.task, todo.closed, todo.id))
                                  .run.transact(xa).unsafeRunSync)

  def delete(id: Int): Count = Count(deleteTodo.toUpdate0(id).run.transact(xa).unsafeRunSync)
}

object TodoRepository {
  val selectTodos = sql"select * from todo order by opened desc".query[Todo]
  val insertTodo = Update[(String, Long, Long)]("insert into todo(task, opened, closed) values (?, ?, ?)")
  val updateTodo = Update[(String, Long, Int)]("update todo set task = ?, closed = ? where id = ?")
  val deleteTodo = Update[Int]("delete from todo where id = ?")

  def apply(xa: Transactor[IO], schema: String): TodoRepository = new TodoRepository(xa, schema)
}