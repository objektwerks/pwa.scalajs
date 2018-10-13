package todo

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser._
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TodoRestClient(todosUrl: String) {
  val headers = Map("Content-Type" -> "application/json; charset=utf-8", "Accept" -> "application/json")

  def listTodos(): Future[List[Todo]] = {
    Ajax.get(url = todosUrl, headers = headers).map { xhr =>
      println(s"listTodos: todos as json text > ${xhr.responseText}")
      decode[List[Todo]](xhr.responseText) match {
        case Right(todos) => todos
        case Left(error) => println(s"listTodos: error > ${error.printStackTrace()}"); List.empty[Todo]
      }
    }
  }

  def addTodo(todo: Todo): Future[Id] = {
    println(s"addTodo: todo as json > ${todo.asJson.toString}")
    Ajax.post(url = todosUrl, headers = headers, data = todo.asJson.toString).map { xhr =>
      println(s"addTodo: id as json text > ${xhr.responseText}")
      decode[Id](xhr.responseText) match {
        case Right(id) => id
        case Left(error) => println(s"addTodo: error > ${error.getMessage}"); Id(0)
      }
    }
  }

  def updateTodo(todo: Todo): Future[Count] = {
    println(s"updateTodo: todo as json > ${todo.asJson.toString}")
    Ajax.put(url = todosUrl, headers = headers, data = todo.asJson.toString).map { xhr =>
      println(s"updateTodo: count as json text > ${xhr.responseText}")
      decode[Count](xhr.responseText) match {
        case Right(count) => count
        case Left(error) => println(s"updateTodo: error > ${error.getMessage}"); Count(0)
      }
    }
  }

  def removeTodo(todoId: Int): Future[Count] = {
    println(s"removeTodo: id > $todoId")
    val uri = todosUrl + "/" + todoId
    Ajax.delete(url = uri, headers = headers).map { xhr =>
      println(s"removeTodo: count as json text > ${xhr.responseText}")
      decode[Count](xhr.responseText) match {
        case Right(count) => count
        case Left(error) => println(s"removeTodo: error > ${error.getMessage}"); Count(0)
      }
    }
  }
}

object TodoRestClient {
  def apply(todosUrl: String): TodoRestClient = new TodoRestClient(todosUrl)
}