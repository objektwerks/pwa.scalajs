package todo

import java.time.Instant

import org.scalajs.dom._
import org.scalajs.dom.raw.{HTMLInputElement, HTMLSelectElement, HTMLSpanElement}
import todo.Todo._

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

class TodoModelView(todoRestClient: TodoRestClient) {
  val todos = mutable.Map.empty[String, Todo]

  val todoList = document.getElementById("todo-list")
  val addTodo = document.getElementById("add-todo").asInstanceOf[html.Input]
  val todoId = document.getElementById("todo-id").asInstanceOf[html.Input]
  val todoOpened = document.getElementById("todo-opened").asInstanceOf[html.Input]
  val todoClosed = document.getElementById("todo-closed").asInstanceOf[html.Input]
  val todoTask = document.getElementById("todo-task").asInstanceOf[html.Input]

  todoList.addEventListener("click", event => onClickTodoList(event))
  addTodo.addEventListener("change", event => onChangeAddTodo(event))
  todoClosed.addEventListener("change", event => onChangeTodoClosed(event))
  todoTask.addEventListener("change", event => onChangeTodoTask(event))

  def init(): Unit = {
    todoRestClient.listTodos().map { listOfTodos =>
      println(s"init: array of todos > $listOfTodos")
      for (todo <- listOfTodos) {
        todos += todo.id.toString -> todo
      }
      setTodoList()
    }
    ()
  }

  def setTodoList(): Unit = {
    println(s"setTodoList: todos > $todos")
    unsetTodoInputs()
    for ((id, todo) <- todos.toSet) {
      val span = document.createElement("span")
      span.setAttribute("id", id)
      span.setAttribute("onclick", "parentElement.style.display='none'")
      span.setAttribute("class", "w3-button w3-transparent w3-display-right")
      span.innerHTML = "&times;"
      span.addEventListener("click", event => onClickRemoveTodo(event))

      val li = document.createElement("li")
      li.appendChild(document.createTextNode(todo.task))
      li.setAttribute("id", id)
      li.setAttribute("class", "w3-display-container")
      li.appendChild(span)
      todoList.appendChild(li)
    }
    ()
  }

  def setTodoInputs(id: String): Unit = {
    val todo = todos(id)
    todoId.value = todo.id.toString
    todoOpened.value = timeStampToDateTimeLocal(todo.opened)
    todoClosed.value = timeStampToDateTimeLocal(todo.closed)
    todoTask.value = todo.task
    todoClosed.readOnly = false
    todoTask.readOnly = false
    todoClosed.setAttribute("class", "w3-input w3-white w3-hover-light-gray")
    todoTask.setAttribute("class", "w3-input w3-white w3-hover-light-gray")
    ()
  }

  def timeStampToDateTimeLocal(timestamp: Long): String = {
    val iso = Instant.ofEpochMilli(timestamp).toString
    iso.substring(0, iso.lastIndexOf(":"))
  }

  def unsetTodoInputs(): Unit = {
    todoList.innerHTML = ""
    addTodo.value = ""
    todoId.value = "0"
    todoOpened.value = ""
    todoClosed.value = ""
    todoTask.value = ""
    todoClosed.readOnly = true
    todoTask.readOnly = true
    todoClosed.setAttribute("class", "w3-input w3-light-gray w3-hover-light-gray")
    todoTask.setAttribute("class", "w3-input w3-light-gray w3-hover-light-gray")
    ()
  }

  def onClickTodoList(event: Event): Unit = {
    val target = event.target.asInstanceOf[HTMLSelectElement]
    println(s"onClickTodoList: click > ${target.id} > ${target.value}")
    ()
  }

  def onChangeAddTodo(event: Event): Unit = {
    val target = event.target.asInstanceOf[HTMLInputElement]
    println(s"onChangeAddTodo: change > ${target.id} > ${target.value}")
    ()
  }

  def onClickRemoveTodo(event: Event): Unit = {
    val target = event.target.asInstanceOf[HTMLSpanElement]
    println(s"onClickRemoveTodo: click > ${target.id}")
    ()
  }

  def onChangeTodoClosed(event: Event): Unit = {
    val target = event.target.asInstanceOf[HTMLInputElement]
    println(s"onChangeTodoClosed: change > ${target.id} > ${target.value}")
    ()
  }

  def onChangeTodoTask(event: Event): Unit = {
    val target = event.target.asInstanceOf[HTMLInputElement]
    println(s"onChangeTodoTask: change > ${target.id} > ${target.value}")
    ()
  }
}

object TodoModelView {
  def apply(todoRestClient: TodoRestClient): TodoModelView = new TodoModelView(todoRestClient)
}