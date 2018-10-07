package todo

import org.scalajs.dom._

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("todo.TodoClient")
object TodoClient{
  def init(): Unit = {
    val content = document.getElementById("content")
    val p = document.createElement("p")
    val text = document.createTextNode("Todo!")
    val node = p.appendChild(text)
    content.appendChild(node)
    ()
  }
}