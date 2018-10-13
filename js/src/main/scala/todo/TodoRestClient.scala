package todo

import io.circe.syntax._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.ext.Ajax.InputData
import todo.implicits.TodoCirceImplicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TodoRestClient(todosUrl: String) {
  val headers = Map("Content-Type" -> "application/json; charset=utf-8", "Accept" -> "application/json")

  def listTodos(): Future[List[Todo]] = {
    Ajax.get(url = todosUrl, withCredentials = true, headers = headers).map { xhr =>
      println(s"listTodos: todos as json > ${xhr.responseText.asJson.toString}")
      xhr.responseText.asJson.as[List[Todo]].getOrElse(List.empty[Todo])
    }
  }

  def addTodo(todo: Todo): Future[Id] = {
    println(s"addTodo: todo as json > ${todo.asJson.toString}")
    Ajax.post(url = todosUrl,
      withCredentials = true,
      headers = headers,
      data = todo.asJson.toString.asInstanceOf[InputData]).map { xhr =>
      xhr.responseText.asJson.as[Id].getOrElse(Id(0))
    }
  }

  def updateTodo(todo: Todo): Future[Count] = {
    println(s"updateTodo: todo as json > ${todo.asJson.toString}")
    Ajax.put(url = todosUrl,
      withCredentials = true,
      headers = headers,
      data = todo.asJson.toString.asInstanceOf[InputData]).map { xhr =>
      xhr.responseText.asJson.as[Count].getOrElse(Count(0))
    }
  }

  def removeTodo(todoId: Int): Future[Count] = {
    println(s"removeTodo: id > $todoId")
    val uri = todosUrl + "/" + todoId
    Ajax.delete(url = uri, withCredentials = true, headers = headers).map { xhr =>
      xhr.responseText.asJson.as[Count].getOrElse(Count(0))
    }
  }
}

object TodoRestClient {
  def apply(todosUrl: String): TodoRestClient = new TodoRestClient(todosUrl)
}