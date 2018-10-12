package todo

import io.circe.syntax._
import org.scalajs.dom.ext.Ajax
import todo.implicits.TodoCirceImplicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TodoRestClient(todosUrl: String) {
  val headers = Map("Content-Type" -> "application/json; charset=utf-8", "Accept" -> "application/json")

  def listTodos(): Future[List[Todo]] = {
    Ajax.get(url = todosUrl, headers = headers).map { xhr =>
      xhr.responseText.asInstanceOf[List[Todo]]
    }
  }

  def addTodo(todo: Todo): Future[Id] = {
    Ajax.post(url = todosUrl, headers = headers, data = todo.asJson.noSpaces).map { xhr =>
      xhr.responseText.asInstanceOf[Id]
    }
  }

  def updateTodo(todo: Todo): Future[Count] = {
    Ajax.put(url = todosUrl, headers = headers, data = todo.asJson.noSpaces).map { xhr =>
      xhr.responseText.asInstanceOf[Count]
    }
  }

  def removeTodo(todoId: Int): Future[Count] = {
    val uri = todosUrl + "/" + todoId
    Ajax.delete(url = uri, headers = headers).map { xhr =>
      xhr.responseText.asInstanceOf[Count]
    }
  }
}

object TodoRestClient {
  def apply(todosUrl: String): TodoRestClient = new TodoRestClient(todosUrl)
}