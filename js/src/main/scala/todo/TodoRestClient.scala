package todo

import io.circe.syntax._
import org.scalajs.dom.ext.Ajax
import todo.implicits.TodoCirceImplicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TodoRestClient(todosUrl: String) {
  val headers = Map("Content-Type" -> "application/json; charset=utf-8", "Accept" -> "application/json")

  def listTodos(): Future[List[Todo]] = Ajax.get(url = todosUrl, headers = headers).map { xhr =>
    xhr.responseText.asJson.as[List[Todo]].getOrElse(List.empty[Todo])
  }

  def addTodo(todo: Todo): Future[Id] = Ajax.post(url = todosUrl, headers = headers, data = todo.asJson.noSpaces).map { xhr =>
    xhr.responseText.asJson.as[Id].getOrElse(Id(0))
  }

  def updateTodo(todo: Todo): Future[Count] = Ajax.put(url = todosUrl, headers = headers, data = todo.asJson.noSpaces).map { xhr =>
    xhr.responseText.asJson.as[Count].getOrElse(Count(0))
  }

  def removeTodo(todo: Todo): Future[Count] = Ajax.delete(url = todosUrl, headers = headers, data = todo.asJson.noSpaces).map { xhr =>
    xhr.responseText.asJson.as[Count].getOrElse(Count(0))
  }
}

object TodoRestClient {
  def apply(todosUrl: String): TodoRestClient = new TodoRestClient(todosUrl)
}