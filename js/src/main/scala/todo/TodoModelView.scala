package todo

import org.scalajs.dom._
import org.scalajs.dom.raw.{HTMLInputElement, HTMLSelectElement, HTMLSpanElement}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

class TodoModelView(todoRestClient: TodoRestClient) {
  val todos = mutable.SortedMap.empty[Int, Todo]

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
      println(s"init: list of todos > ${listOfTodos.toString}")
      for (todo <- listOfTodos) {
        todos += (todo.id -> todo)
      }
      println(s"init: map of todos > ${todos.toString}")
      setTodoList()
    }
    ()
  }

  def setTodoList(): Unit = {
    println(s"setTodoList: todos > $todos")
    unsetTodoInputs()
    for ((id,todo) <- todos) {
      val span = document.createElement("span")
      span.setAttribute("id", id.toString)
      span.setAttribute("onclick", "parentElement.style.display='none'")
      span.setAttribute("class", "w3-button w3-transparent w3-display-right")
      span.innerHTML = "&times;"
      span.addEventListener("click", event => onClickRemoveTodo(event))

      val li = document.createElement("li")
      li.appendChild(document.createTextNode(todo.task))
      li.setAttribute("id", id.toString)
      li.setAttribute("class", "w3-display-container")
      li.appendChild(span)
      todoList.appendChild(li)
    }
    ()
  }

  def timestampToDate(timestamp: Long): String = {
    val iso = new js.Date(timestamp.toDouble).toISOString()
    val isoDateTime = iso.substring(0, iso.lastIndexOf(":"))
    println(s"iso: $iso")
    println(s"iso datetime: $isoDateTime")
    isoDateTime
  }

  def setTodoInputs(id: Int): Unit = {
    val todo = todos(id)
    todoId.value = todo.id.toString
    todoOpened.value = timestampToDate(todo.opened)
    todoClosed.value = timestampToDate(todo.closed)
    todoTask.value = todo.task
    todoClosed.readOnly = false
    todoTask.readOnly = false
    todoClosed.setAttribute("class", "w3-input w3-white w3-hover-light-gray")
    todoTask.setAttribute("class", "w3-input w3-white w3-hover-light-gray")
    ()
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
    println(s"onClickTodoList: click > ${target.id}")
    setTodoInputs(target.id.toInt)
  }

  def onChangeAddTodo(event: Event): Unit = {
    val target = event.target.asInstanceOf[HTMLInputElement]
    println(s"onChangeAddTodo: change > ${target.value}")
    val task = target.value
    if (task != null && task.nonEmpty) {
      val todo = Todo(task = task)
      todoRestClient.addTodo(todo).map { id =>
        if (id.value > 0) {
          val timestamp = new js.Date().getTime.toLong
          val newTodo = todo.copy(id = id.value, opened = timestamp, closed = timestamp)
          todos += (id.value -> newTodo)
          println(s"onChangeAddTodo: new todo > $newTodo")
          setTodoList()
        } else {
          println(s"onChangeAddTodo: failed > $task")
        }
      }
    }
    ()
  }

  def onClickRemoveTodo(event: Event): Unit = {
    val target = event.target.asInstanceOf[HTMLSpanElement]
    println(s"onClickRemoveTodo: click > ${target.id}")
    val todo = todos(target.id.toInt)
    todoRestClient.removeTodo(todo.id).map { count =>
      if (count.value == 1) {
        todos -= target.id.toInt
        setTodoList()
        println(s"onClickRemoveTodo: removed > $todo")
      } else {
        println(s"onClickRemoveTodo: failed > $todo")
      }
    }
    ()
  }

  def onChangeTodoClosed(event: Event): Unit = {
    val target = event.target.asInstanceOf[HTMLInputElement]
    println(s"onChangeTodoClosed: change > ${target.id} > ${target.value}")
    var todo = todos(todoId.value.toInt)
    val timestamp = new js.Date(target.value).getTime.toLong
    todo = todo.copy(closed = timestamp)
    onChangeUpdateTodo(todo)
  }

  def onChangeTodoTask(event: Event): Unit = {
    val target = event.target.asInstanceOf[HTMLInputElement]
    println(s"onChangeTodoTask: change > ${target.id} > ${target.value}")
    var todo = todos(todoId.value.toInt)
    val task = target.value
    if (task != null && task.nonEmpty) {
      todo = todo.copy(task = target.value)
      onChangeUpdateTodo(todo)
    } else {
      target.value = todo.task
    }
  }

  def onChangeUpdateTodo(todo: Todo): Unit = {
    todoRestClient.updateTodo(todo).map { count =>
      if (count.value > 0) {
        todos(todo.id) = todo
        setTodoList()
        setTodoInputs(todo.id)
        println(s"onChangeUpdateTodo: updated > $todo")
      } else {
        println(s"onChangeUpdateTodo: update failed > $todo")
      }
    }
    ()
  }
}

object TodoModelView {
  def apply(todoRestClient: TodoRestClient): TodoModelView = new TodoModelView(todoRestClient)
}