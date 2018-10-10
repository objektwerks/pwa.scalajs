package todo

import java.sql.Timestamp
import java.time.Instant

import cats.effect.IO
import com.typesafe.config.ConfigFactory
import doobie.scalatest._
import doobie.util.transactor.Transactor
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.client.blaze.Http1Client
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.middleware.GZip
import org.http4s.{Method, Request, Uri}
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class TodoTest extends FunSuite with BeforeAndAfterAll with IOChecker {
  import todo.Todo._
  import todo.implicits.TodoHttp4sCirceImplicits._

  val conf = ConfigFactory.load("test.conf")
  val xa = Transactor.fromDriverManager[IO](conf.getString("test.driver"), conf.getString("test.url"), conf.getString("test.user"), conf.getString("test.password"))
  val repository = TodoRepository(xa, conf.getString("test.schema"))
  val service = TodoService(repository).instance
  val server = BlazeBuilder[IO]
    .bindHttp(conf.getInt("test.port"), conf.getString("test.host"))
    .mountService(GZip(service), "/api/v1")
    .start
    .unsafeRunSync
  val client = Http1Client[IO]().unsafeRunSync
  val todosUri = Uri.unsafeFromString("http://localhost:7979/api/v1/todos")

  override def transactor: Transactor[IO] = xa

  override protected def afterAll(): Unit = {
    server.shutdownNow
    client.shutdownNow
  }

  test("check") {
    import TodoRepository._

    check(selectTodos)
    check(insertTodo)
    check(updateTodo)
    check(deleteTodo)
  }

  test("post") {
    val todo = Todo(task = "buy beer")
    val id = post(todo)
    println(id)
    println(id.asJson)
    assert(id.id == 1)
  }

  test("get") {
    val todos = get
    assert(todos.length == 1)
  }

  test("put") {
    val todo = get.head
    assert(todo.id == 1)
    val completedTodo = todo.copy(closed = Timestamp.from(Instant.now).getTime)
    assert(put(completedTodo).count == 1)
  }

  test("delete") {
    val todos = get
    println(todos)
    println(todos.asJson)
    val todo = todos.head
    println(todo)
    println(todo.asJson)
    println(todos)
    println(todos.asJson)
    val count = delete(todo.id)
    println(count)
    println(count.asJson)
    assert(count.count == 1)
    assert(get.isEmpty)
    assert(post(Todo(task = "drink beer")).id == 2)
  }

  def post(todo: Todo): Id = {
    val post = Request[IO](Method.POST, todosUri).withBody(todo.asJson)
    client.expect[Id](post).unsafeRunSync
  }

  def get: List[Todo] = {
    val get = Request[IO](Method.GET, todosUri)
    client.expect[List[Todo]](get).unsafeRunSync
  }

  def put(todo: Todo): Count = {
    val put = Request[IO](Method.PUT, todosUri).withBody(todo.asJson)
    client.expect[Count](put).unsafeRunSync
  }

  def delete(id: Int): Count = {
    val url = s"${todosUri.toString}/$id"
    val delete = Request[IO](Method.DELETE, Uri.unsafeFromString(url))
    client.expect[Count](delete).unsafeRunSync
  }
}