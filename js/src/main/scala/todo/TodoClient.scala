package todo

import org.scalajs.dom._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("TodoClient")
object TodoClient{
  @JSExport
  def init(): Unit = {
    version()
  }

  def version(): Unit = {
    val version = document.getElementById("version")
    val p = document.createElement("p")
    val text = document.createTextNode("V1")
    val node = p.appendChild(text)
    version.appendChild(node)
    ()
  }
}