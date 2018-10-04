package todo

import org.scalajs.dom._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("TodoClient")
object TodoClient{

  @JSExport
  def main(args: Array[String]): Unit = {
    val content = document.getElementById("content")
    val p = document.createElement("p")
    val text = document.createTextNode("Todo!")
    val node = p.appendChild(text)
    content.appendChild(node)
    ()
  }
}