package todo

import org.scalajs.dom.ext.Ajax
import todo.Todo._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSON

class TodoRestClient(todosUrl: String) {
  val headers = Map("Content-Type" -> "application/json; charset=utf-8", "Accept" -> "application/json")

  def listTodos(): Future[js.Array[Todo]] = Ajax.get(url = todosUrl, headers = headers).map { xhr =>
    JSON.parse(xhr.responseText).asInstanceOf[js.Array[Todo]]
  }

  def addTodo(todo: Todo): Future[Id] = Ajax.post(url = todosUrl, headers = headers, data = toJson(todo)).map { xhr =>
    JSON.parse(xhr.responseText).asInstanceOf[Id]
  }

  def updateTodo(todo: Todo): Future[Count] = Ajax.put(url = todosUrl, headers = headers, data = toJson(todo)).map { xhr =>
    JSON.parse(xhr.responseText).asInstanceOf[Count]
  }

  def removeTodo(todo: Todo): Future[Count] = Ajax.delete(url = todosUrl, headers = headers, data = toJson(todo)).map { xhr =>
    JSON.parse(xhr.responseText).asInstanceOf[Count]
  }

  def toJson(todo: Todo): String = {
    JSON.stringify(js.Dynamic.literal("id" -> todo.id, "task" -> todo.task, "opened" -> todo.opened.getTime, "closed" -> todo.closed.getTime))
  }
}

object TodoRestClient {
  def apply(todosUrl: String): TodoRestClient = new TodoRestClient(todosUrl)
}