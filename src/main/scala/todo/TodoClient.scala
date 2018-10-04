package todo

import org.scalajs.dom._

object TodoClient{
  def main(args: Array[String]): Unit = {
    val content = document.getElementById("content")
    val p = document.createElement("p")
    val text = document.createTextNode("Todo!")
    val node = p.appendChild(text)
    content.appendChild(node)
    ()
  }
}